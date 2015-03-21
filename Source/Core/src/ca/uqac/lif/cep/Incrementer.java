/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2015 Sylvain Hall�

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
package ca.uqac.lif.cep;

import java.util.Vector;

public class Incrementer extends SingleProcessor
{
	protected final float m_increment;
	
	public Incrementer(float increment)
	{
		super(1, 1);
		m_increment = increment;
	}

	@Override
	protected Vector<Object> compute(Vector<Object> inputs)
	{
		Vector<Object> outputs = new Vector<Object>();
		for (Object in : inputs)
		{
			if (in instanceof Number)
			{
				Number n = (Number) in;
				n = n.floatValue() + m_increment;
				outputs.add(n);
			}
		}
		return outputs;
	}

}
