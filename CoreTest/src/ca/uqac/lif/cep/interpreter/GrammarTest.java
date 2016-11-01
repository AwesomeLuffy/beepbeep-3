/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.interpreter;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.interpreter.Interpreter.ParseException;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.FileHelper;
import ca.uqac.lif.cep.util.PackageFileReader;

@RunWith(Parameterized.class)
public class GrammarTest 
{

	static final String[] s_queries = readQueries();

	protected Interpreter m_interpreter = new Interpreter();
	
	int m_queryNumber = 0;
	
	public GrammarTest(int query_number)
	{
		super();
		m_queryNumber = query_number;
	}
	
	static String[] readQueries()
	{
		String file_contents;
		try 
		{
			file_contents = PackageFileReader.readPackageFile(GrammarTest.class.getResourceAsStream("all-queries.esql"));
			String[] queries = file_contents.split("---");
			return queries;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return new String[0];
	}

	@Test
	public void debugQueryNumber() throws ParseException
	{
		String query = s_queries[m_queryNumber];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		m_interpreter.setDebugMode(true, ps);
		m_interpreter.addPlaceholder("@foo", "processor", new QueueSource());
		m_interpreter.addPlaceholder("@bar", "processor", new QueueSource());
		try
		{
			Pullable p = m_interpreter.executeQueries(query);
			if (p == null)
			{
				fail("Parsing failed on expression " + query);
			}
		}
		catch (ParseException e)
		{
			FileHelper.writeFromBytes(new File("/home/sylvain/debug.txt"), baos.toByteArray());
			throw e;
		}
	}

	@Parameters(name = "{index}: query {0}")
	public static Collection<Integer[]> getQueries()
	{
		List<Integer[]> ints = new ArrayList<Integer[]>(s_queries.length);
		for (int i = 0; i < s_queries.length; i++)
		{
			Integer[] a_i = new Integer[1];
			a_i[0] = i;
			ints.add(a_i);
		}
		return ints;
	}
}
