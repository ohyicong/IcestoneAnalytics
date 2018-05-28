/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.PolymorphicInstrumentCode;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Owner
 */
public class FeedosInstrumentCodesParserHelper 
{
	public static PolymorphicInstrumentCode buildInstrumentCodeFromString(String code)
	{
		int index1 = code.indexOf('@');
		int index2 = code.indexOf('/');
		if (0 <= index1 && index1 < code.length())
		{	// code is MIC@LocalCodeStr
			return new PolymorphicInstrumentCode(code);
		}
		if (0 <= index2 && index2 < code.length())
		{	// code is MktId/InstrId
			String[] codeParts = code.split("/");
			int marketId = Integer.decode(codeParts[0]).intValue(); 
			int localCode = Integer.decode(codeParts[1]).intValue();
			int internalCode = PolymorphicInstrumentCode.build_internal_code(marketId, localCode, 0);
			return new PolymorphicInstrumentCode(internalCode);
		}
		else
		{	// code is an internal code
			return new PolymorphicInstrumentCode(Integer.decode(code).intValue());
		}
	}

	/**
	 * @brief parse a string containing either instruments or a file name that contains instrument codes
	 * 
	 * @param code_argument contains the codes or a file name containing the instrument codes
	 * @return an array of Polymorphic instrument codes, that may be empty
	 */
	public static ArrayList<PolymorphicInstrumentCode> parse(String instrument_codes_specification)
	{
		FileReader file_reader = null;
		ArrayList<PolymorphicInstrumentCode> instr_codes;
		try
		{
			file_reader = new FileReader(instrument_codes_specification);	
			instr_codes = new ArrayList<PolymorphicInstrumentCode>();
			BufferedReader in = new BufferedReader(file_reader);			
		    try 
		    {
		    	String this_line;
		        while ((this_line = in.readLine()) != null) 
		        { 
		        	instr_codes.add(buildInstrumentCodeFromString(this_line));
		        } 
		    } 
		    catch (IOException e) 
		    {
		       System.err.println("Error: " + e);
			   return null;
		    }
		    finally
		    {
		    	try
		    	{
		    		in.close();
		    	}
		    	catch (IOException e)
		    	{
		    	}
		    }
		}
		catch(FileNotFoundException e)
		{
			instr_codes = new ArrayList<PolymorphicInstrumentCode>();
			StringTokenizer tokenizer = new StringTokenizer(instrument_codes_specification,",");
			while (tokenizer.hasMoreElements()) 
			{
				instr_codes.add(buildInstrumentCodeFromString(tokenizer.nextToken()));
			}
		}
		finally
		{
			try
			{
				if (file_reader != null)
				{
					file_reader.close();
				}
			}
			catch (IOException e)
			{
			}
		}
		
		return instr_codes;
	}
}

