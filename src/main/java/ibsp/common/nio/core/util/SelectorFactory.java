package ibsp.common.nio.core.util;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.EmptyStackException;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temp selector factory,come from grizzly
 */
public class SelectorFactory {

	private static Logger logger = LoggerFactory.getLogger(SelectorFactory.class);

	public static final int DEFAULT_MAX_SELECTORS = 20;

	/**
	 * The timeout before we exit.
	 */
	public final static long timeout = 5000;

	/**
	 * The number of <code>Selector</code> to create.
	 */
	private static int maxSelectors;

	/**
	 * Cache of <code>Selector</code>
	 */
	private final static Stack<Selector> selectors = new Stack<Selector>();

	/**
	 * Creates the <code>Selector</code>
	 */
	static {
		try {
			setMaxSelectors(DEFAULT_MAX_SELECTORS);
		} catch (IOException ex) {
			logger.error("SelectorFactory initializing Selector pool, exception: {}", ex.getMessage());
		}
	}

	/**
	 * Set max selector pool size.
	 * 
	 * @param size
	 *            max pool size
	 */
	public final static void setMaxSelectors(int size) throws IOException {
		synchronized (selectors) {
			if (size < maxSelectors) {
				reduce(size);
			} else if (size > maxSelectors) {
				grow(size);
			}

			maxSelectors = size;
		}
	}

	/**
	 * Returns max selector pool size
	 * 
	 * @return max pool size
	 */
	public final static int getMaxSelectors() {
		return maxSelectors;
	}

	/**
	 * Get a exclusive <code>Selector</code>
	 * 
	 * @return <code>Selector</code>
	 */
	public final static Selector getSelector() {
		synchronized (selectors) {
			Selector s = null;
			try {
				if (selectors.size() != 0) {
					s = selectors.pop();
				}
			} catch (EmptyStackException ex) {
			}

			int attempts = 0;
			try {
				while (s == null && attempts < 2) {
					selectors.wait(timeout);
					try {
						if (selectors.size() != 0) {
							s = selectors.pop();
						}
					} catch (EmptyStackException ex) {
						break;
					}
					attempts++;
				}
			} catch (InterruptedException ex) {
			}
			return s;
		}
	}

	/**
	 * Return the <code>Selector</code> to the cache
	 * 
	 * @param s
	 *            <code>Selector</code>
	 */
	public final static void returnSelector(Selector s) {
		synchronized (selectors) {
			selectors.push(s);
			if (selectors.size() == 1) {
				selectors.notify();
			}
		}
	}

	/**
	 * Increase <code>Selector</code> pool size
	 */
	private static void grow(int size) throws IOException {
		for (int i = 0; i < size - maxSelectors; i++) {
			selectors.add(Selector.open());
		}
	}

	/**
	 * Decrease <code>Selector</code> pool size
	 */
	private static void reduce(int size) {
		for (int i = 0; i < maxSelectors - size; i++) {
			try {
				Selector selector = selectors.pop();
				selector.close();
			} catch (IOException e) {
				logger.error("SelectorFactory.reduce");
				logger.error(e.getMessage(), e);
			}
		}
	}

}