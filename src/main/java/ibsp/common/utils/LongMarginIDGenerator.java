package ibsp.common.utils;

public class LongMarginIDGenerator {
	
	public static LongMargin nextMargin(String seqName, int step) {
		LongMargin longMargin = null;
		
		int retry = 0;
		while (retry < CONSTS.SEQ_RETRY_CNT) {
			longMargin = BasicOperation.nextSeqMargin(seqName, step);
			if (longMargin != null)
				break;
		}
		
		return longMargin;
	}
	
	public static class LongMargin {
		
		private long start;
		private long end;
		
		public LongMargin(long start, long end) {
			super();
			this.start = start;
			this.end = end;
		}
		
		public long getStart() {
			return start;
		}
		public void setStart(long start) {
			this.start = start;
		}
		public long getEnd() {
			return end;
		}
		public void setEnd(long end) {
			this.end = end;
		}
		
	}

}
