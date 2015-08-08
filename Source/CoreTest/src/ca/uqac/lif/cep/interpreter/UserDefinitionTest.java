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
package ca.uqac.lif.cep.interpreter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.eml.tuples.EmlNumber;
import ca.uqac.lif.cep.eml.tuples.TupleGrammar;
import ca.uqac.lif.cep.interpreter.Interpreter.ParseException;

public class UserDefinitionTest 
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
	public void testDefinition1() throws ParseException
	{
		String expression = "WHEN @P IS A processor: THE COUNT OF ( @P ) IS THE processor COMBINE (SELECT 1 FROM @P) WITH SUM";
		Object o = m_interpreter.parseLanguage(expression);
		assertNotNull(o);
		assertTrue(o instanceof Interpreter.UserDefinition);
		Interpreter.UserDefinition user_def = (Interpreter.UserDefinition) o;
		user_def.addToInterpreter();
		// Now, parse an expression that uses this definition
		String user_expression = "THE COUNT OF (0)";
		//m_interpreter.setDebugMode(true);
		Object user_stmt = m_interpreter.parseLanguage(user_expression);
		assertTrue(user_stmt instanceof Processor);
		Pullable p = ((Processor) user_stmt).getPullableOutput(0);
		// Pull a tuple from the resulting processor
		Object answer = p.pull();
		assertNotNull(answer);
		assertTrue(answer instanceof EmlNumber);
		EmlNumber num = (EmlNumber) answer;
		assertEquals(1, num.numberValue().intValue());
		// Pull another
		num = (EmlNumber) p.pull();
		assertEquals(2, num.numberValue().intValue());
		// Pull another
		num = (EmlNumber) p.pull();
		assertEquals(3, num.numberValue().intValue());
	}
	
	@Test
	public void testDefinition2() throws ParseException
	{
		String expression = "PI IS THE eml_number 3.1416";
		Object o = m_interpreter.parseLanguage(expression);
		assertNotNull(o);
		assertTrue(o instanceof Interpreter.UserDefinition);
		Interpreter.UserDefinition user_def = (Interpreter.UserDefinition) o;
		user_def.addToInterpreter();
		// Now, parse an expression that uses this definition
		String user_expression = "PI";
		Object user_stmt = m_interpreter.parseLanguage(user_expression);
		assertTrue(user_stmt instanceof Processor);
		Pullable p = ((Processor) user_stmt).getPullableOutput(0);
		// Pull a tuple from the resulting processor
		Object answer = p.pull();
		assertNotNull(answer);
		assertTrue(answer instanceof EmlNumber);
		EmlNumber num = (EmlNumber) answer;
		assertEquals(3.1416, num.numberValue().floatValue(), 0.01);
		// Pull another
		num = (EmlNumber) p.pull();
		assertEquals(3.1416, num.numberValue().floatValue(), 0.01);
	}
}
