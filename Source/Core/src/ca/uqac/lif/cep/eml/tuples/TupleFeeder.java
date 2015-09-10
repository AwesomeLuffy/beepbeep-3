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

import java.util.Stack;
import java.util.Vector;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.input.TokenFeeder;

/**
 * Creates a feed of events from CRLF-separated string chunks.
 * Note that the input feed must have a trailing CRLF for all elements,
 * including the last. 
 * @author sylvain
 *
 */
public class TupleFeeder extends TokenFeeder
{
	Vector<String> m_names;
	
	public TupleFeeder()
	{
		super();
		m_names = new Vector<String>();
		m_separatorBegin = "";
		m_separatorEnd = "\n";
	}
	
	@Override
	protected Object createTokenFromInput(String token)
	{
		token = token.trim();
		if (token.isEmpty() || token.startsWith("#"))
		{
			// Ignore comment and empty lines
			return new TokenFeeder.NoToken();
		}
		String[] parts = token.split(",");
		if (m_names.isEmpty())
		{
			// This is the first token we read; it contains the names
			// of the arguments
			for (String part : parts)
			{
				m_names.add(part);
			}
			return new TokenFeeder.NoToken();
		}
		int i = 0;
		NamedTuple out_tuple = new NamedTuple();
		for (String att_name : m_names)
		{
			if (i >= parts.length)
			{
				// Silently ignore missing parameters, although this
				// should not happen
				break;
			}
			String value = parts[i];
			EmlConstant eml_value = EmlConstant.createConstantFromString(value);
			out_tuple.put(att_name, eml_value);
			i++;
		}
		return out_tuple;
	}

	@Override
	public void build(Stack<Object> stack)
	{
		Processor p = (Processor) stack.pop();
		stack.pop(); // OF
		stack.pop(); // TUPLES
		stack.pop(); // THE
		Connector.connect(p, this);
		stack.push(this);
	}

}
