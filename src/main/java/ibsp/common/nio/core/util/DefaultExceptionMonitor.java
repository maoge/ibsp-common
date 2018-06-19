package ibsp.common.nio.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default {@link ExceptionMonitor} implementation that logs uncaught
 * exceptions using {@link Logger}.
 * <p>
 * All {@link IoService}s have this implementation as a default exception
 * monitor.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 */
public class DefaultExceptionMonitor extends ExceptionMonitor {

	private static Logger logger = LoggerFactory.getLogger(DefaultExceptionMonitor.class);

	@Override
	public void exceptionCaught(final Throwable cause) {
		logger.error(cause.getMessage());
	}
}