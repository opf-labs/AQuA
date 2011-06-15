using System;
using System.Collections.Generic;
using System.Text;

namespace MetsParser
{
    class Program
    {
        static void Main(string[] args)
        {
            new ParseMetsFiles(@"c:\Aqua\AQuA\15June11\aqua\", @"c:\Aqua\AQuA\15June11\aqua\Analyse.log");
        }
    }
}
