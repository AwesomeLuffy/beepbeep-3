/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2015 Sylvain Hallé

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
package ca.uqac.lif.cep;

/**
 * Function of one input and one output
 * @param <T> The type of the input
 * @param <U> The type of the output
 */
public abstract class UnaryFunction<T,U> implements Function 
{
	@SuppressWarnings("unchecked")
	@Override
	/*@ requires inputs.length == 1 */
	public Object[] compute(/*@NonNull*/ Object[] inputs) 
	{
		Object[] out = new Object[1];
		out[0] = evaluate((T) inputs[0]);
		return out;
	}
	
	/**
	 * Evaluates the function
	 * @param x The argument
	 * @return The return value of the function
	 */
	public abstract U evaluate(T x); 

	@Override
	public final int getInputArity() 
	{
		return 1;
	}

	@Override
	public final int getOutputArity() 
	{
		return 1;
	}
	
	@Override
	public void reset()
	{
		// Do nothing
	}
	
	@Override
	public UnaryFunction<T,U> clone()
	{
		return this;
	}

}
