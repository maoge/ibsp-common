package ibsp.common.utils;

import ibsp.common.utils.LongMarginIDGenerator.LongMargin;

import java.util.concurrent.locks.ReentrantLock;

public class LongMarginID {
	
	private static final int MARGIN_STEP = 10000;
	
	private String seqName;
	private long start;
	private long end;
	private int step;
	private volatile long currID = -1;
	
	private ReentrantLock lock;
	
	public LongMarginID(String seqName, int step) {
		this.seqName = seqName;
		this.step = step < 1 ? MARGIN_STEP : step;
		
		nextMargin();
		this.lock = new ReentrantLock();
	}
	
	private void nextMargin() {
		LongMargin margin = LongMarginIDGenerator.nextMargin(seqName, step);
		
		this.start = margin.getStart();
		this.end = margin.getEnd();
		this.currID = start;
	}
	
	public long nextID() {
		long id = -1;
		
		try {
			lock.lock();
			
			id = currID++;
			if (currID > end) {
				nextMargin();
			}
			
		} finally {
			lock.unlock();
		}
		
		return id;
	}

}
