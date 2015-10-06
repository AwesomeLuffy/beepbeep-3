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

public class Division extends BinaryExpression
{
	protected static final Division s_singleton = new Division();
	
	public static Division getSingleton()
	{
		return s_singleton;
	}
	
	@Override
	public EmlConstant evaluate(Object t_left, Object t_right)
	{
		float n_left = EmlNumber.parseFloat(t_left);
		float n_right = EmlNumber.parseFloat(t_right);
		return new EmlNumber(n_left / n_right);
	}
}
