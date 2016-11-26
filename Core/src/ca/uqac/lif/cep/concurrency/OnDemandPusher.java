package ca.uqac.lif.cep.concurrency;

import ca.uqac.lif.cep.Pushable;

public class OnDemandPusher implements Pusher
{
	protected Pushable m_pushable;
	
	boolean m_run = false;

	protected long s_sleepInterval = 100;

	private Call m_currentCall = Call.NONE;

	private Object m_eventToPush = null;
	
	private boolean m_done = false;
	
	public OnDemandPusher(Pushable p)
	{
		super();
		m_pushable = p;
	}
	
	@Override
	public void run() 
	{
		m_run = true;
		while (m_run)
		{
			switch (m_currentCall)
			{
			case PUSH:
				m_pushable.push(m_eventToPush);
				break;
			default:
				break;
			}
			m_currentCall = Call.NONE;
			m_done = true;
		}
		OnDemandPoller.sleep(s_sleepInterval);
	}
	
	public void setEventToPush(Object o)
	{
		m_eventToPush = o;
	}

	@Override
	public boolean isDone() 
	{
		return m_done;
	}

	@Override
	synchronized public void call(Call c)
	{
		m_done = false;
		m_currentCall = c;
	}

	@Override
	public void stop() 
	{
		m_run = false;
	}

	@Override
	public Pushable getPushable() 
	{
		return m_pushable;
	}

}
