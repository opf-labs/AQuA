using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;

namespace MetsParser
{
    public class IssueList
    {
        //
        // Issue Errors
        //
        internal enum IssueError { None = 0x00, Gap = 0x01, IssueSequence = 0x02 };
        //
        // ArrayList To Hold Issues
        //
        ArrayList ArrayListIssues;
        //
        // Class To Contain List Of Issues
        //
        public IssueList()
        {
            ArrayListIssues = new ArrayList();
        }
        //
        // Add New Issue To List
        //
        internal void AddIssue(StreamWriter StreamWriterLog, String StrMetsFile, String StrMetsIssue, String StrMetsVolume, String StrMetsDate, String StrModsTitle)
        {
            ArrayListIssues.Add(new Issue(StreamWriterLog, StrMetsFile, StrMetsIssue, StrMetsVolume, StrMetsDate, StrModsTitle));
            return;
        }
        //
        // Sort Issues Into Date Order
        //
        internal void Sort()
        {
            int iIssueCount = ArrayListIssues.Count;
            //
            // Allocate Arrays
            //
            long[] lArrayListDates = new long[iIssueCount];
            int[] iArrayListIndex = new int[iIssueCount];
            //
            // Load Arrays
            //
            for (int iListInx = 0; iListInx < iIssueCount; iListInx += 1)
            {
                Issue IssueThis = (Issue)ArrayListIssues[iListInx];
                lArrayListDates[iListInx] = IssueThis.Ticks();
                iArrayListIndex[iListInx] = iListInx;
            }
            //
            // Sort
            //
            Array.Sort(lArrayListDates, iArrayListIndex);
            //
            // Create New ArrayList & Load
            //
            ArrayList ArrayListIssuesNew = new ArrayList();
            for (int iListInx = 0; iListInx < iIssueCount; iListInx += 1)
            {
                ArrayListIssuesNew.Add(ArrayListIssues[iArrayListIndex[iListInx]]);
            }
            //
            // Copy New ArrayList To Old
            //
            ArrayListIssues = ArrayListIssuesNew;
            //
            return;
        }
        //
        // Display Title
        //
        internal void DisplayTitle(StreamWriter StreamWriterLog)
        {
            Issue IssueThis = (Issue)ArrayListIssues[0];
            if (IssueThis != null)
            {
                WriteLine(StreamWriterLog, "");
                Console.Out.WriteLine();
                WriteLine(StreamWriterLog, IssueThis.GetTitle());
                Console.Out.WriteLine(IssueThis.GetTitle());
            }
            return;
        }
        //
        // Report Assume Sorted
        //
        internal void Report(StreamWriter StreamWriterLog)
        {
            int iIssue = -1;
            int iIssueOld = -1;
            Issue IssueThis = null;
            Issue IssueOld = null;
            double[] dDaysOfWeek = new double[7];
            for (int iListInx = 0; iListInx < ArrayListIssues.Count; iListInx += 1)
            {
                //
                // Get Issue
                //
                IssueOld = IssueThis;
                IssueThis = (Issue)ArrayListIssues[iListInx];
                dDaysOfWeek[(int )IssueThis.GetIssueDate().DayOfWeek] += 1.0;
                //
                // Check Issue Sequence If Year Difference Is Equal Or Less Than 1
                //
                if ((IssueThis != null) && (IssueOld != null) &&
                    ((Math.Abs(IssueThis.GetIssueDate().Year - IssueOld.GetIssueDate().Year) <= 1)))
                {
                    iIssueOld = iIssue;
                    iIssue = IssueThis.GetIssue();
                    if ((iIssueOld != -1) && ((iIssueOld + 1) != iIssue))
                    {
                        IssueOld.SetErrorFlag(IssueError.IssueSequence);
                    }
                }
                //
            }
            //
            // Output Issues Data
            //
            Issue IssueGood = null;
            bool fTimeGapError = false;
            for (int iListInx = 0; iListInx < ArrayListIssues.Count; iListInx += 1)
            {
                //
                // Get Issue
                //
                IssueOld = IssueThis;
                IssueThis = (Issue)ArrayListIssues[iListInx];
                DateTime DateTimeThis = IssueThis.GetIssueDate();
                //
                // Write Output
                //
                Write(StreamWriterLog, IssueThis.GetNLP());
                if (fTimeGapError == true)
                    WriteLine(StreamWriterLog, " " + IssueThis.GetStrDate() + " Publication Pattern Error - Issue Not Expected On This Date");
                else
                {
                    WriteLine(StreamWriterLog, " " + IssueThis.GetStrDate() + " ok");
                    IssueGood = IssueThis;
                }
                fTimeGapError = false;
                IssueError IssueErrorThis = IssueThis.GetErrorFlag();
                if (IssueErrorThis == (IssueError.Gap | IssueError.IssueSequence))
                {
                    //
                    // Number Of Issues Missing
                    //
                    Issue IssueNext = (Issue)ArrayListIssues[iListInx + 1];
                    DateTime DateTimeNext = IssueNext.GetIssueDate();
                    int iIssuesToInsert = IssueNext.GetIssue() - IssueThis.GetIssue() - 1;
                    //
                    // Average Number Of Days Between Publications
                    //
                    double dAverageDays = IssueThis.GetAverageDays();
                    if (dAverageDays < 6.0)
                    {
                        //
                        // Day Of The Week For Valid Publication
                        //
                        int iDayOfWeek = (int )DateTimeThis.DayOfWeek;
                        //
                        // Threshold Number Of Publications To Make This Day Of The Week A Legitimate Publication Day
                        //
                        double dDayOfWeekThreshold = dDaysOfWeek[iDayOfWeek] * 0.6;
                        DateTime DateTimeGood = IssueGood.GetIssueDate();
                        int iDayCount = 0;
                        for (int iInsertInx = 0; iInsertInx < iIssuesToInsert; iInsertInx += 1)
                        {
                            iDayOfWeek = (iDayOfWeek + 1) % 7;
                            iDayCount += 1;
                            if (dDaysOfWeek[iDayOfWeek] > dDayOfWeekThreshold)
                            {
                                DateTime DateTimeNew = DateTimeGood.AddDays((double)iDayCount);
                                if (DateTimeNew < DateTimeNext)
                                {
                                    //
                                    // Write Additional Record
                                    //
                                    Write(StreamWriterLog, IssueThis.GetNLP());
                                    WriteLine(StreamWriterLog, " " + DateTimeNew.ToString("yyyy-MM-dd") + " Inserted Entry Date and Issue Number Gap Found");
                                    //
                                }
                            }
                        }
                    }
                    else
                    {
                        int iWeekGap = (int)((dAverageDays + 4) / 7);
                        DateTime DateTimeGood = IssueGood.GetIssueDate();
                        DateTime DateTimeNew = DateTimeGood.AddDays((double)(iWeekGap * 7));
                        while (DateTimeNew < DateTimeNext)
                        {
                            //
                            // Write Additional Record
                            //
                            Write(StreamWriterLog, IssueThis.GetNLP());
                            WriteLine(StreamWriterLog, " " + DateTimeNew.ToString("yyyy-MM-dd") + " Inserted Entry Date and Issue Number Gap Found");
                            DateTimeNew = DateTimeNew.AddDays((double)(iWeekGap * 7));
                            //
                        }
                        //
                        // Get Next Date - Set Error Flag If Not Next Issue Date
                        //
                        TimeSpan TimeSpanDays = DateTimeNext - DateTimeNew;
                        if (TimeSpanDays.Days != 0)
                        {
                            fTimeGapError = true;
                        }
                    }
                }
                //
            }
            //
            return;
        }
        //
        // Write Line
        //
        void WriteLine(StreamWriter StreamWriterLog, String StrLine)
        {
            if (StreamWriterLog != null)
                StreamWriterLog.WriteLine(StrLine);
            else
                Console.Out.WriteLine(StrLine);
            return;
        }
        //
        // Write
        //
        void Write(StreamWriter StreamWriterLog, String StrLine)
        {
            if (StreamWriterLog != null)
                StreamWriterLog.Write(StrLine);
            else
                Console.Out.Write(StrLine);
            return;
        }
        //
        // Do Publication Pattern Analysis
        //
        internal void PublicationPattern(StreamWriter StreamWriterLog)
        {
            //
            // Get Day of Week Information For Each Year In Turn
            //
            int[] iDayOfWeekCount = new int[7];
            int iYear = -1;
            int iYearBegInx = 0;
            int iYearEndInx = 0;
            for (int iListInx = 0; iListInx < ArrayListIssues.Count; iListInx += 1)
            {
                //
                // Get Issue & Day Of Week
                //
                Issue IssueThis = (Issue)ArrayListIssues[iListInx];
                DateTime DateTimeIssue = IssueThis.GetIssueDate();
                //
                // Initalise Year
                //
                if (iYear == -1)
                    iYear = DateTimeIssue.Year;
                //
                // Determine If Year Has Changed
                //
                if (iYear != DateTimeIssue.Year)
                {
                    iYearEndInx = iListInx - 1;
                    //
                    // Compute Average Number Of Days Between Issues For This Year
                    //
                    double dAverageDaysBetweeenIssues = 365 / (double)(iYearEndInx - iYearBegInx + 1);
                    //
                    // Go Through List Again Looking For Gap Between Issues
                    //
                    int iDayOfYear = -1;
                    Issue IssueYearOld = null;
                    Issue IssueYear = null;
                    for (int iYearInx = iYearBegInx; iYearInx <= iYearEndInx; iYearInx += 1)
                    {
                        IssueYearOld = IssueYear;
                        IssueYear = (Issue)ArrayListIssues[iYearInx];
                        DateTime DateTimeYear = IssueYear.GetIssueDate();
                        if (iDayOfYear == -1)
                            iDayOfYear = DateTimeYear.DayOfYear;
                        else
                        {
                            int iDayDifference = Math.Abs(DateTimeYear.DayOfYear - iDayOfYear);
                            if ((iDayDifference > (dAverageDaysBetweeenIssues * 1.3)) ||
                                (iDayDifference < (dAverageDaysBetweeenIssues * 0.8)))
                            {
                                IssueYearOld.SetErrorFlag(IssueError.Gap);
                                IssueYearOld.SetAverageDays(dAverageDaysBetweeenIssues);
                                //Console.Out.WriteLine("Error Big Gap In Publication Dates, Average = " + dAverageDaysBetweeenIssues);
                                //Console.Out.WriteLine(IssueYearOld.GetStrDate() + " File: " + IssueYearOld.GetFileName());
                                //Console.Out.WriteLine(IssueYear.GetStrDate() + " File: " + IssueYear.GetFileName());
                                //StreamWriterLog.WriteLine("Error Big Gap In Publication Dates, Average = " + dAverageDaysBetweeenIssues);
                                //StreamWriterLog.WriteLine(IssueYearOld.GetStrDate() + " File: " + IssueYearOld.GetFileName());
                                //StreamWriterLog.WriteLine(IssueYear.GetStrDate() + " File: " + IssueYear.GetFileName());
                            }
                            iDayOfYear = DateTimeYear.DayOfYear;
                        }
                    }
                    //
                    iYearBegInx = iListInx;
                    iYear = DateTimeIssue.Year;
                }
            }
            return;
        }
        //
        // Number Of Issues
        //
        internal int Count
        {
            get { return (ArrayListIssues.Count); }
        }
        //
        // Issue Class
        //
        internal class Issue
        {
            //
            // Passed Values
            //
            String StrMetsFile;
            String StrMetsIssue;
            String StrMetsVolume;
            String StrMetsDate;
            String StrModsTitle;
            //
            // Computed Values
            //
            int iIssue;
            int iVolume;
            DateTime DateTimeIssue;
            DateTime DateTimeFileName;
            double dAverageDays;
            //
            // Error Flag
            //
            IssueError IssueErrorThis;
            //
            // Initalisation
            //
            internal Issue(StreamWriter StreamWriterLog, String StrMetsFile, String StrMetsIssue, String StrMetsVolume, String StrMetsDate, String StrModsTitle)
            {
                this.StrMetsFile = StrMetsFile;
                this.StrMetsIssue = StrMetsIssue;
                this.StrMetsVolume = StrMetsVolume;
                this.StrMetsDate = StrMetsDate;
                this.StrModsTitle = StrModsTitle;
                //
                // Error Flag
                //
                IssueErrorThis = IssueError.None;
                //
                // Convert To Numbers If Possible
                //
                iIssue = GetNumber(StrMetsIssue);
                iVolume = GetNumber(StrMetsVolume);
                DateTimeIssue = GetDate(StrMetsDate);
                //
                // Parse FileName For Date
                //
                DateTimeFileName = GetFileNameDate(StrMetsFile);
                //
                // Compare Year, Month & Day
                //
                if ((DateTimeIssue.Year != DateTimeFileName.Year) ||
                    (DateTimeIssue.Month != DateTimeFileName.Month) ||
                    (DateTimeIssue.Day != DateTimeFileName.Day))
                {
                    Console.Out.WriteLine("Error: Date Mismatch Between Mods Filename: " + StrMetsFile + " and Mods File Content: " + DateTimeIssue.ToString());
                    StreamWriterLog.WriteLine("Error: Date Mismatch Between Mods Filename: " + StrMetsFile + " and Mods File Content: " + DateTimeIssue.ToString());
                }
                //
            }
            //
            // Get Number
            //
            int GetNumber(String StrValue)
            {
                int iValue = -1;
                if (StrValue != null)
                {
                    try
                    {
                        iValue = int.Parse(StrValue);
                    }
                    catch { }
                }
                return (iValue);
            }
            //
            // Get Date yyyy-mm-dd
            //
            DateTime GetDate(String StrDate)
            {
                DateTime DateTimeThis = new DateTime(1, 1, 1);
                //
                //                   0123456789     
                // Date Is In Format YYYY-MM-DD
                //
                int iYear = -1;
                int iMonth = -1;
                int iDay = -1;
                if (StrDate.Length >= 10)
                {
                    iYear = GetNumber(StrDate.Substring(0, 4));
                    iMonth = GetNumber(StrDate.Substring(5, 2));
                    iDay = GetNumber(StrDate.Substring(8, 2));
                }
                //
                // Ensure Values Are Reasonable
                //
                if ((iYear > 0) && (iMonth > 0) && (iMonth <= 12) && (iDay > 0) && (iDay <= 31))
                {
                    DateTimeThis = new DateTime(iYear, iMonth, iDay);
                }
                //
                return (DateTimeThis);
            }
            //
            // Get Date From File Name
            //
            DateTime GetFileNameDate(String StrMetsFile)
            {
                DateTime DateTimeThis = new DateTime(1, 1, 1);
                //
                //       9876543211987654321
                // nnnnnnn_yyyymmdd_mets.xml (nnnnnnn - NLP; yyyy - Year; mm - Month; dd - Day)
                // 0000187_18200106_mets.xml 
                //
                int iYear = -1;
                int iMonth = -1;
                int iDay = -1;
                if (StrMetsFile.Length >= 25)
                {
                    int iLength = StrMetsFile.Length;
                    iYear = GetNumber(StrMetsFile.Substring(iLength - 17, 4));
                    iMonth = GetNumber(StrMetsFile.Substring(iLength - 13, 2));
                    iDay = GetNumber(StrMetsFile.Substring(iLength - 11, 2));
                }
                //
                // Ensure Values Are Reasonable
                //
                if ((iYear > 0) && (iMonth > 0) && (iMonth <= 12) && (iDay > 0) && (iDay <= 31))
                {
                    DateTimeThis = new DateTime(iYear, iMonth, iDay);
                }
                // 
                return (DateTimeThis);
            }
            //
            // Ticks For Issue Date
            //
            internal long Ticks()
            {
                return (DateTimeIssue.Ticks);
            }
            //
            // Get StrDate
            //
            internal String GetStrDate()
            {
                String StrDate = DateTimeIssue.ToString("yyyy-MM-dd");
                return (StrDate);
            }
            //
            // Get Date
            //
            internal DateTime GetIssueDate()
            {
                return (DateTimeIssue);
            }
            //
            // Get Issue
            //
            internal int GetIssue()
            {
                return (iIssue);
            }
            //
            // Get FileName
            //
            internal String GetFileName()
            {
                return (StrMetsFile);
            }
            //
            // Get Title
            //
            internal String GetTitle()
            {
                return (StrModsTitle);
            }
            //
            // Set Error Flag
            //
            internal void SetErrorFlag(IssueError IssueErrorThis)
            {
                this.IssueErrorThis |= IssueErrorThis;
                return;
            }
            //
            // Get Error Flag
            //
            internal IssueError GetErrorFlag()
            {
                return (IssueErrorThis);
            }
            //
            // Get NLP
            //
            internal String GetNLP()
            {
                int iMetsXml = StrMetsFile.IndexOf("_mets.xml");
                String StrNLP = "";
                if (iMetsXml >= 16)
                    StrNLP = StrMetsFile.Substring(iMetsXml - 16, 7);
                return (StrNLP);
            }
            //
            // Set Average Days
            //
            internal void SetAverageDays(double dAverageDays)
            {
                this.dAverageDays = dAverageDays;
                return;
            }
            //
            // Get Average Days
            //
            internal double GetAverageDays()
            {
                return (dAverageDays);
            }
        }
    }
}
