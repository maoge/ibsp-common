package ibsp.common.utils;

import java.util.concurrent.locks.ReentrantLock;

public class IBSPConfig {
	
	private static final ReentrantLock monitor = new ReentrantLock();
	private static IBSPConfig instance = null;
	
	private String metasvrUrl          = "";
	private String metasvrUserid       = "";
	private String metasvrUserpwd      = "";
	
	// cache properties
	private int cachePoolSize          = 1;
	private String cacheConnectionMode = "sync";
	private int cacheRedisProxyTimeout = 30;
	private String cacheServiceID      = "";
	
	// db properties
	private String dbServiceID         = "";
	private String dbDriver            = "com.mysql.jdbc.Driver";
	private String dbName;
	private String dbProperties;  // something like characterEncoding=utf8&useSSL=true
	private String dbUsername;
	private String dbPassword;
	
	private int dbMaxPoolSize          = 10;
	private int dbMinPoolSize          = 1;
	private int dbInitPoolSize         = 5;
	private int dbConnectionTimeout    = 3000;
	private int dbMaxIdleTime          = 10*60*1000;
	private int dbMaxLifetime          = 60*60*1000;
	private int dbValidationTimeout    = 5*1000;
	private int dbIdleConnectionTestPeriod = 60*1000;
	private boolean dbIsAutoCommit     = false;
	private String dbConnectionTestQuery = "select 1 from dual";
	
	// mq properties
	private String mqType              = "rabbitmq";
	private boolean mqZklockerSupport  = false;
	private String mqZookeeperRooturl  = "127.0.0.1:2181";
	private boolean mqDebug            = false;
	private boolean mqPubConfirm       = false;
	private int mqPrefetchSize         = CONSTS.PREFETCH_COUNT;
	private int mqRouterMultiplexingRatio = CONSTS.MULTIPLEXING_RATIO;
	private long mqWriteTimeout        = CONSTS.WRITE_TIMEOUT;

	
	public static IBSPConfig getInstance() {
		try {
			monitor.lock();
			
			if(instance==null) instance = new IBSPConfig();
		} finally {
    		monitor.unlock();
    	}
		
		return instance;
	}
	
	private IBSPConfig() {
		PropertiesUtils props = PropertiesUtils.getInstance(CONSTS.INIT_PROP_FILE);
		
		this.metasvrUrl             = props.get(CONSTS.METASVR_ROOTURL, "");
		this.metasvrUserid          = props.get(CONSTS.METASVR_USERID, "");
		this.metasvrUserpwd         = DES3.decrypt(props.get(CONSTS.METASVR_USERPWD, ""));
		
		// cache properties
		this.cachePoolSize          = props.getInt(CONSTS.CACHE_POOL_SIZE, 1);
		this.cacheConnectionMode    = props.get(CONSTS.CACHE_CONNECTION_MODE, "sync");
        this.cacheRedisProxyTimeout = props.getInt(CONSTS.CACHE_REDIS_PROXY_TIMEOUT, 30);
        this.cacheServiceID         = props.get(CONSTS.CACHE_SERVICE_ID, "");
        
        // db properties
        this.dbServiceID            = props.get(CONSTS.DB_SERVICE_ID);
        this.dbDriver               = props.get(CONSTS.DB_DRIVER);
		this.dbName                 = props.get(CONSTS.DB_NAME);
		this.dbProperties           = props.get(CONSTS.DB_PROPS, "");
		this.dbUsername             = props.get(CONSTS.DB_USERNAME, "");
		this.dbPassword             = props.get(CONSTS.DB_PWD, "");
		
		this.dbMaxPoolSize 	        = props.getInt(CONSTS.DB_MAX_POOL_SIZE, 10);
		this.dbMinPoolSize 	        = props.getInt(CONSTS.DB_MIN_POOL_SIZE, 1);
		this.dbInitPoolSize 	    = props.getInt(CONSTS.DB_INIT_POOL_SIZE, 5);
		this.dbMaxIdleTime 	        = props.getInt(CONSTS.DB_MAX_IDLE_TIME, 10*60*1000);
		this.dbMaxLifetime 	        = props.getInt(CONSTS.DB_MAX_LIFE_TIME, 60*60*1000);
		this.dbConnectionTimeout    = props.getInt(CONSTS.DB_CONN_TIMEOUT, 3000);
		this.dbValidationTimeout    = props.getInt(CONSTS.DB_VALIDATION_TIMEOUT, 5000);
		this.dbIdleConnectionTestPeriod = props.getInt(CONSTS.DB_IDLE_CONN_TEST_PERIOD, 60000);
		this.dbIsAutoCommit         = props.getBoolean(CONSTS.DB_IS_AUTO_COMMIT, false);
		this.dbConnectionTestQuery  = props.get(CONSTS.DB_CONN_TEST_QUERY, "select 1 from dual");
		
		// mq properties
		this.mqType                 = props.get(CONSTS.MQ_CONF_TYPE);
		this.mqZklockerSupport      = props.getBoolean(CONSTS.MQ_CONF_ZKLOKER_SUPPORT, false);
		this.mqZookeeperRooturl     = props.get(CONSTS.MQ_CONF_ZK);
		this.mqPubConfirm           = props.getBoolean(CONSTS.MQ_CONF_PUBCONFIRM, false);
		this.mqPrefetchSize         = props.getInt(CONSTS.MQ_CONF_PRETETCHSIZE, CONSTS.PREFETCH_COUNT);
		this.mqRouterMultiplexingRatio = props.getInt(CONSTS.MQ_CONF_MULTIPLEXING_RATIO, CONSTS.MULTIPLEXING_RATIO);
		this.mqWriteTimeout         = props.getLong(CONSTS.MQ_CONF_WRITE_TIMEOUT, CONSTS.WRITE_TIMEOUT);
		this.mqDebug                = props.getBoolean(CONSTS.MQ_DEBUG, false);
		
	}
	
	public String getMetasvrUrl() {
		return metasvrUrl;
	}
	
	public int getCachePoolSize() {
		return cachePoolSize;
	}

	public String getCacheConnectionMode() {
		return cacheConnectionMode;
	}

	public int getCacheRedisProxyTimeout() {
		return cacheRedisProxyTimeout;
	}

	public String getCacheServiceID() {
		return cacheServiceID;
	}
	
	public String getDbServiceID() {
		return dbServiceID;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbProperties() {
		return dbProperties;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public int getDbMaxPoolSize() {
		return dbMaxPoolSize;
	}

	public int getDbMinPoolSize() {
		return dbMinPoolSize;
	}

	public int getDbInitPoolSize() {
		return dbInitPoolSize;
	}

	public int getDbConnectionTimeout() {
		return dbConnectionTimeout;
	}

	public int getDbMaxIdleTime() {
		return dbMaxIdleTime;
	}

	public int getDbMaxLifetime() {
		return dbMaxLifetime;
	}

	public int getDbValidationTimeout() {
		return dbValidationTimeout;
	}

	public int getDbIdleConnectionTestPeriod() {
		return dbIdleConnectionTestPeriod;
	}

	public boolean DbIsAutoCommit() {
		return dbIsAutoCommit;
	}

	public String getDbConnectionTestQuery() {
		return dbConnectionTestQuery;
	}
	
	public String getMqZKRootUrl() {
		return mqZookeeperRooturl;
	}

	public String getMqType() {
		return mqType;
	}

	public boolean MqIsPubConfirm() {
		return mqPubConfirm;
	}

	public boolean MqIsDebug() {
		return mqDebug;
	}

	public String getMetaSvrUserId() {
		return metasvrUserid;
	}
	
	public void setMetaSvrUserId(String userId) {
		this.metasvrUserid = userId;
	}

	public String getMetaSvrUserPwd() {
		return metasvrUserpwd;
	}
	
	public void setMetaSvrUserPwd(String passwd) {
		this.metasvrUserpwd = passwd;
	}

	public int getMqPrefetchSize() {
		return mqPrefetchSize;
	}

	public int getMqMultiplexingRatio() {
		return mqRouterMultiplexingRatio;
	}

	public boolean MqIsMqZKLockerSupport() {
		return mqZklockerSupport;
	}

	public long getMqWriteTimeout() {
		return mqWriteTimeout;
	}

}
