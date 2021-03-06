package ibsp.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesUtils {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class.getName());
	
	public static final String CONF_PATH = "conf";
	
	private Properties prop = null;
	
	private static HashMap<String, PropertiesUtils> prosMap;
	
	public static synchronized PropertiesUtils getInstance(String propName) {
		PropertiesUtils instance = null;

		if (prosMap == null) {
			prosMap = new HashMap<String, PropertiesUtils>();
		}

		instance = (PropertiesUtils) prosMap.get(propName);

		if (instance == null) {
			instance = new PropertiesUtils(propName);
			prosMap.put(propName, instance);
		}

		return instance;
	}
	
	private PropertiesUtils(String fileName) {
		fileName = chkPropertiesName(fileName);
		this.prop = new Properties();
		loadResource(this.prop, fileName);
	}
	
	private void loadResource(Properties properties, String fileName) {
		InputStream istream = null;
		try {
			if (new File(fileName).exists()) {
				istream = new FileInputStream(fileName);
			} else {
				istream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(fileName);
			}
			this.prop.load(istream);
		} catch (Exception e) {
			logger.error(PropertiesUtils.class.getName()
					+ "there is no found resource file of the name ["
					+ fileName + "]", e);
			throw new RuntimeException(
					"there is no found resource file of the name [" + fileName
							+ "]", e);
		}
		closeStream(istream);
	}
	
	private void closeStream(InputStream istream) {
		if (istream == null)
			return;
		try {
			istream.close();
			return;
		} catch (IOException e) {
			logger.error(PropertiesUtils.class.getName(), e);
		} finally {
			try {
				if (istream != null) {
					istream.close();
				}
			} catch (Exception e) {
				logger.error(PropertiesUtils.class.getName(), e);
			}
		}
	}
	
	public void addResource(String name) {
		loadResource(this.prop, name);
	}
	
	public Properties getProperties() {
		return this.prop;
	}
	
	public Object set(String key, String value) {
		return this.prop.setProperty(key, value);
	}
	
	public String get(String key) {
		return this.prop.getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return this.prop.getProperty(key) == null ? defaultValue : this.prop
				.getProperty(key);
	}
	
	public int getInt(String key) {
		return Integer.parseInt(this.prop.getProperty(key));
	}
	
	public int getInt(String key, int defaultValue) {
		return this.prop.getProperty(key) == null ? defaultValue : Integer
				.parseInt(this.prop.getProperty(key));
	}
	
	public long getLong(String key) {
		return Long.parseLong(this.prop.getProperty(key));
	}
	
	public long getLong(String key, long defaultValue) {
		return this.prop.getProperty(key) == null ? defaultValue : Long
				.parseLong(this.prop.getProperty(key));
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(this.prop.getProperty(key));
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		return this.prop.getProperty(key) == null ? defaultValue : Boolean
				.parseBoolean(this.prop.getProperty(key));
	}
	
	public static String chkPropertiesName(String fileName) {
		Properties props = System.getProperties();
		String separator = props.getProperty("file.separator");
		File rootDir = new File("").getAbsoluteFile();

		String nameStr = null;
		if ((fileName.contains(".")) && (fileName.endsWith(".properties"))) {
			nameStr = fileName;
		} else {
			nameStr = fileName + ".properties";
		}

		String _rootNameStr = rootDir + separator + nameStr;
		String _binNameStr = rootDir + separator + "bin" + separator + nameStr;
		String _confNameStr = rootDir + separator + "conf" + separator + nameStr;
		
		if (new File(_rootNameStr).exists())
			return _rootNameStr;
		if (new File(_binNameStr).exists())
			return _binNameStr;
		if (new File(_confNameStr).exists()) {
			return _confNameStr;
		}
		
		// 找不到则按ClassLoader找
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		java.net.URL fromRoot = cl.getResource(nameStr);
		java.net.URL fromBin = cl.getResource("bin");
		java.net.URL fromConf = cl.getResource("conf");
		if (fromRoot != null) {
			return fromRoot.getPath();
		}
		
		if (fromBin != null) {
			String base = fromBin.getFile();
			File file = new File(base + separator + nameStr);
			if (file != null) {
				return file.getPath();
			}
		}
		
		if (fromConf != null) {
			String base = fromConf.getFile();
			File file = new File(base + separator + nameStr);
			if (file != null) {
				return file.getPath();
			}
		}
		
		return nameStr;
	}
	
	public static String getConfFilePath(String fileName) {
		Properties props = System.getProperties();
		String separator = props.getProperty("file.separator");
		File rootDir = new File("").getAbsoluteFile();
		String path = rootDir.getAbsolutePath();
		
		if (path.endsWith("bin")) {
			path += separator + "..";
		}
		
		path += separator + CONF_PATH + separator + fileName;
		return path;
	}
	
}
