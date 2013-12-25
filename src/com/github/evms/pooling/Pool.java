package com.github.evms.pooling;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A pool to be used with any object. Ideally should be used with those that
 * implement Poolable.
 * 
 * @author Christopher Capps
 * 
 * @param <T>
 *            The class to use with the Pool.
 */
public abstract class Pool<T> {

	private ConcurrentLinkedQueue<T> objects;

	abstract protected T createObject();

	/**
	 * Create a new Pool.
	 */
	public Pool() {
		objects = new ConcurrentLinkedQueue<T>();
	}

	/**
	 * Get an object from the pool.
	 * 
	 * @return
	 */
	public synchronized T getObject() {
		if (objects.size() == 0) {
			objects.add(createObject());
		}

		return objects.remove();
	}

	/**
	 * Free the object and place into the pool.
	 * 
	 * @param object
	 */
	public void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		if (object instanceof Poolable) {
			((Poolable) object).reset();
		}
		objects.add(object);
	}
}
