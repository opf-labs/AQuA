using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;

namespace MetsParser
{
    public class ParseMetsFiles
    {
        //
        // Parser Values
        //
        enum MetsParse { None, Part, Issue, Volume, OriginInfo, TitleInfo }
        //
        // Parse Files In Mets Directories
        //
        public ParseMetsFiles(String StrMetsDirectory, String StrLogFile)
        {
            StreamWriter StreamWriterLog = null;
            StreamWriterLog = new StreamWriter(StrLogFile);
            //
            // Directory Structure is <<Root>>\<NLP>\<Year>\<Date>\<Files>
            // <NLP> is of the form ddddddd (d - digit)
            // <Year> is of the form yyyy (y - year digit)
            // <Date> is of the form mmdd (m - month digit, d - day digit>
            //
            String[] StrNLP = Directory.GetDirectories(StrMetsDirectory);
            for (int iNLPInx = 0; iNLPInx < StrNLP.Length; iNLPInx += 1)
            {
                //
                // Create List Of Issues
                //
                IssueList IssueListThis = new IssueList();
                //
                // Get Publication Years
                //
                String[] StrYear = Directory.GetDirectories(StrNLP[iNLPInx]);
                for (int iYearInx = 0; iYearInx < StrYear.Length; iYearInx += 1)
                {
                    //
                    // Get Publication Dates
                    //
                    String[] StrDate = Directory.GetDirectories(StrYear[iYearInx]);
                    for (int iDateInx = 0; iDateInx < StrDate.Length; iDateInx += 1)
                    {
                        String[] StrFiles = Directory.GetFiles(StrDate[iDateInx]);
                        //
                        // Find Mets File
                        //
                        String StrMetsFile = null;
                        for (int iFileInx = 0; ((iFileInx < StrFiles.Length) && (StrMetsFile == null)); iFileInx += 1)
                        {
                            if (StrFiles[iFileInx].EndsWith("_mets.xml") == true)
                                StrMetsFile = StrFiles[iFileInx];
                        }
                        //Console.Out.WriteLine();
                        //Console.Out.WriteLine("Mets File: " + StrMetsFile);
                        if (StrMetsFile != null)
                            ParseMetsFile(StreamWriterLog, StrMetsFile, IssueListThis);
                        else
                            Console.Out.WriteLine("No Mets File " + StrDate[iDateInx]);
                        //
                    }
                }
                //
                // Only Process If Number Of Issues Greater Than 0
                //
                if (IssueListThis.Count > 0)
                {
                    //
                    // Sort Issues
                    //
                    IssueListThis.Sort();
                    //
                    // Display Newspaper Title
                    //
                    IssueListThis.DisplayTitle(StreamWriterLog);
                    //
                    // Do Publication Pattern Analysis
                    //
                    IssueListThis.PublicationPattern(StreamWriterLog);
                    //
                    // Report Errors
                    //
                    IssueListThis.Report(StreamWriterLog);
                }
                //
            }
            //
            // Stop Window Closing
            //
            if (StreamWriterLog != null)
                StreamWriterLog.Close();
            //Console.In.Read();
            //
        }
        //
        // Parse Mets File
        //
        void ParseMetsFile(StreamWriter StreamWriterLog, String StrMetsFile, IssueList IssueListThis)
        {
            //
            // Create Xml File Reader
            //
            XmlReader XmlReaderMets = XmlReader.Create(new StreamReader(StrMetsFile));
            //
            // Parse File
            //
            MetsParse MetsParseCurrent = MetsParse.None;
            String StrMetsIssue = null;
            String StrMetsVolume = null;
            String StrMetsDate = null;
            String StrModsTitle = null;
            //
            // Use fEnd To Terminate Early
            //
            bool fEnd = false;
            while ((fEnd == false) && (XmlReaderMets.Read() == true))
            {
                //
                // Parse Elements
                //
                if (XmlReaderMets.NodeType == XmlNodeType.Element)
                {
                    switch (MetsParseCurrent) 
                    {
                        case MetsParse.None:
                            //
                            // Look For <mods:part><mods:detail type="issue">
                            //
                            if (XmlReaderMets.Name == "mods:part")
                                MetsParseCurrent = MetsParse.Part;
                            //
                            // Look For <mods:originInfo><mods:dateIssued>
                            //
                            else if (XmlReaderMets.Name == "mods:originInfo")
                                MetsParseCurrent = MetsParse.OriginInfo;
                            //
                            // Look For <mods:titleInfo><mods:title>
                            //
                            else if (XmlReaderMets.Name == "mods:titleInfo")
                                MetsParseCurrent = MetsParse.TitleInfo;
                            //
                            break;
                        case MetsParse.Part:
                            //
                            // If Found <mods:part> Look For <mods:detail>
                            //
                            if (XmlReaderMets.Name == "mods:detail")
                            {
                                //
                                // Select Issue Or Volume
                                //
                                String StrType = XmlReaderMets.GetAttribute("type");
                                if (StrType == "issue")
                                {
                                    MetsParseCurrent = MetsParse.Issue;
                                }
                                else if (StrType == "volume")
                                {
                                    MetsParseCurrent = MetsParse.Volume;
                                }
                            }
                            break;
                        case MetsParse.Issue:
                            //
                            // Get Issue Element
                            //
                            if (XmlReaderMets.Read() == true)
                            {
                                StrMetsIssue = XmlReaderMets.Value;
                                MetsParseCurrent = MetsParse.Part;
                            }
                            break;
                        case MetsParse.Volume:
                            //
                            // Get Volume Element
                            //
                            if (XmlReaderMets.Read() == true)
                            {
                                StrMetsVolume = XmlReaderMets.Value;
                                MetsParseCurrent = MetsParse.Part;
                            }
                            break;
                        case MetsParse.OriginInfo:
                            //
                            // If Found <mods:OriginInfo> Look For <mods:dateIssued>
                            //
                            if (XmlReaderMets.Name == "mods:dateIssued")
                            {
                                if (XmlReaderMets.Read() == true)
                                    StrMetsDate = XmlReaderMets.Value;
                                MetsParseCurrent = MetsParse.None;
                            }
                            break;
                        case MetsParse.TitleInfo:
                            //
                            // If Found <mods:TitleInfo> Look For <mods:title>
                            //
                            if (XmlReaderMets.Name == "mods:title")
                            {
                                if (XmlReaderMets.Read() == true)
                                    StrModsTitle = XmlReaderMets.Value;
                                MetsParseCurrent = MetsParse.None;
                            }
                            break;
                    }
                }
                else if (XmlReaderMets.NodeType == XmlNodeType.EndElement)
                {
                    //
                    // If <mods:part> of <mods:originInfo> Ends - Revert To No Status
                    //
                    if ((MetsParseCurrent == MetsParse.Part) && (XmlReaderMets.Name == "mods:part"))
                        MetsParseCurrent = MetsParse.None;
                    if ((MetsParseCurrent == MetsParse.OriginInfo) && (XmlReaderMets.Name == "mods:originInfo"))
                        MetsParseCurrent = MetsParse.None;
                    if ((MetsParseCurrent == MetsParse.TitleInfo) && (XmlReaderMets.Name == "mods:titleInfo"))
                        MetsParseCurrent = MetsParse.None;
                    if ((StrMetsIssue != null) && (StrMetsDate != null) && (StrModsTitle != null))
                        fEnd = true;
                }
            }
            //Console.Out.WriteLine("Issue: " + StrMetsIssue + " Volume: " + StrMetsVolume + " Date: " + StrMetsDate);
            //
            // Close File
            //
            XmlReaderMets.Close();
            //
            // Add Issue To IssueList
            //
            IssueListThis.AddIssue(StreamWriterLog, StrMetsFile, StrMetsIssue, StrMetsVolume, StrMetsDate, StrModsTitle);
            //
            return;
        }
    }
}
