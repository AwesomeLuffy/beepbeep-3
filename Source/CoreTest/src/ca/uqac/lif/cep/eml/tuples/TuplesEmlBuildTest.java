/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2015 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.eml.tuples;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.cep.interpreter.GrammarExtension;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.interpreter.Interpreter.ParseException;

public class TuplesEmlBuildTest
{
	protected Interpreter m_interpreter;

	@Before
	public void setUp()
	{
		m_interpreter = new Interpreter();		
		GrammarExtension ext = new TupleGrammar();
		m_interpreter.extendGrammar(ext);
	}
	
	@Test
	public void testExtensionProcList1() throws ParseException
	{
		String expression = "0";
		Object result = m_interpreter.parseLanguage(expression, "<eml_proc_list>");
		assertTrue(result instanceof ProcessorDefinitionList);
		assertEquals(1, ((ProcessorDefinitionList) result).m_definitions.size());
	}
	
	@Test
	public void testExtensionProcList2() throws ParseException
	{
		String expression = "0, 1";
		Object result = m_interpreter.parseLanguage(expression, "<eml_proc_list>");
		assertTrue(result instanceof ProcessorDefinitionList);
		assertEquals(2, ((ProcessorDefinitionList) result).m_definitions.size());
	}
	
	@Test
	public void testExtensionProcList3() throws ParseException
	{
		String expression = "0 AS matrace";
		Object result = m_interpreter.parseLanguage(expression, "<eml_proc_list>");
		assertTrue(result instanceof ProcessorDefinitionList);
		assertEquals(1, ((ProcessorDefinitionList) result).m_definitions.size());
	}
	
	@Test
	public void testExtensionProcList3b() throws ParseException
	{
		String expression = "(0) AS matrace";
		Object result = m_interpreter.parseLanguage(expression, "<eml_proc_list>");
		assertTrue(result instanceof ProcessorDefinitionList);
		assertEquals(1, ((ProcessorDefinitionList) result).m_definitions.size());
	}
	
	@Test
	public void testExtensionProcList4() throws ParseException
	{
		String expression = "0 AS matrace, 1 AS matrace";
		Object result = m_interpreter.parseLanguage(expression, "<eml_proc_list>");
		assertTrue(result instanceof ProcessorDefinitionList);
		assertEquals(2, ((ProcessorDefinitionList) result).m_definitions.size());
	}
}
