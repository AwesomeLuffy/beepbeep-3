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
package ca.uqac.lif.cep.concurrency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.PullableWrapper;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.PushableWrapper;

public class ThroughputMeter 
{
	Map<Integer,Set<PushableMeter>> m_pushMeters;
	
	Map<Integer,Set<PullableMeter>> m_pullMeters;
	
	Map<Integer,String> m_descriptions;
	
	public ThroughputMeter()
	{
		super();
		m_pushMeters = new HashMap<Integer,Set<PushableMeter>>();
		m_pullMeters = new HashMap<Integer,Set<PullableMeter>>();
		m_descriptions = new HashMap<Integer,String>();
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append("Mode ID    Description       #      Avg time (ms)\n");
		for (Entry<Integer,Set<PushableMeter>> e : m_pushMeters.entrySet())
		{
			out.append("Push ");
			out.append(pad(e.getKey().toString(), 6));
			out.append(pad(m_descriptions.get(e.getKey()), 18));
			long tot_time = 0;
			long cnt = 0;
			for (PushableMeter meter : e.getValue())
			{
				tot_time += meter.m_totalTime;
				cnt += meter.m_pushCount;
			}
			float avg = 0;
			if (cnt > 0)
			{
				avg = ((float) tot_time / (float) cnt) / 1000000f;
			}
			out.append(pad(Long.toString(cnt), 7));
			out.append(avg).append("\n");
		}
		for (Entry<Integer,Set<PullableMeter>> e : m_pullMeters.entrySet())
		{
			out.append("Pull ");
			out.append(pad(e.getKey().toString(), 6));
			out.append(pad(m_descriptions.get(e.getKey()), 18));
			long tot_time = 0;
			long cnt = 0;
			for (PullableMeter meter : e.getValue())
			{
				tot_time += meter.m_totalTime;
				cnt += meter.m_pulledCount;
			}
			float avg = 0;
			if (cnt > 0)
			{
				avg = ((float) tot_time / (float) cnt) / 1000000f;
			}
			out.append(pad(Long.toString(cnt), 7));
			out.append(avg).append("\n");
		}
		return out.toString();
	}
	
	protected static String pad(String s, int width)
	{
		if (s.length() < width)
		{
			for (int i = s.length(); i < width; i++)
			{
				s += " ";
			}
		}
		return s;
	}
	
	
	public PushableMeter newInputPushMeter(Processor p, int index, ProcessorMeter pm, int original_id, String description)
	{
		m_descriptions.put(original_id, description);
		PushableMeter p_meter = new PushableMeter(p.getPushableInput(index), pm);
		Set<PushableMeter> meters = null;
		if (!m_pushMeters.containsKey(original_id))
		{
			meters = new HashSet<PushableMeter>();
		}
		else
		{
			meters = m_pushMeters.get(original_id);
		}
		meters.add(p_meter);
		m_pushMeters.put(original_id, meters);
		return p_meter;
	}
	
	public PullableMeter newOutputPullMeter(Processor p, int index, Processor reference, int original_id, String description)
	{
		m_descriptions.put(original_id, description);
		PullableMeter p_meter = new PullableMeter(p.getPullableOutput(index), reference);
		Set<PullableMeter> meters = null;
		if (!m_pullMeters.containsKey(original_id))
		{
			meters = new HashSet<PullableMeter>();
		}
		else
		{
			meters = m_pullMeters.get(original_id);
		}
		meters.add(p_meter);
		m_pullMeters.put(original_id, meters);
		return p_meter;
	}
	
	public static class PushableMeter extends PushableWrapper
	{
		int m_pushCount = 0;
		
		long m_totalTime = 0;
		
		long m_minTime = 0;
		
		long m_maxTime = 0;
		
		PushableMeter(Pushable p, Processor reference)
		{
			super(p, reference);
		}
		
		@Override
		public Pushable push(Object o)
		{
			long time_start = System.nanoTime();
			m_pushable.push(o);
			long time_end = System.nanoTime();
			long duration = time_end - time_start;
			m_totalTime += duration;
			m_pushCount++;
			m_minTime = Math.min(m_minTime, duration);
			m_maxTime = Math.max(m_maxTime, duration);
			return this;
		}
	}
	
	public static class PullableMeter extends PullableWrapper
	{
		int m_pulledCount = 0;
		
		long m_totalTime = 0;
		
		long m_minTime = 0;
		
		long m_maxTime = 0;
		
		PullableMeter(Pullable p, Processor reference)
		{
			super(p, reference);
		}
		
		@Override
		public NextStatus hasNextSoft()
		{
			long time_start = System.nanoTime();
			NextStatus s = m_pullable.hasNextSoft();
			long time_end = System.nanoTime();
			long duration = time_end - time_start;
			m_totalTime += duration;
			return s;
		}
		
		@Override
		public boolean hasNext()
		{
			long time_start = System.nanoTime();
			boolean s = m_pullable.hasNext();
			long time_end = System.nanoTime();
			long duration = time_end - time_start;
			m_totalTime += duration;
			return s;
		}
		
		@Override
		public Object pull()
		{
			Object o = m_pullable.pull();
			if (o != null)
			{
				m_pulledCount++;
			}
			return o;
		}
		
		@Override
		public Object pullSoft()
		{
			Object o = m_pullable.pullSoft();
			if (o != null)
			{
				m_pulledCount++;
			}
			return o;
		}
	}
}
