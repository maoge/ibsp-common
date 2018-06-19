package ibsp.common.events;

import com.alibaba.fastjson.JSONObject;

public interface EventSubscriber {
	
	public void postEvent(JSONObject event);
	
	public void doCompute();
	
	public void doReport();

}
