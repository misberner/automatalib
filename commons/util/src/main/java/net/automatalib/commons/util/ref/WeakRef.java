/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.commons.util.ref;

import java.lang.ref.WeakReference;


/**
 * A weak reference wrapper, complying to the {@link Ref} interface.
 * 
 * @author Malte Isberner
 *
 * @param <T> referent class.
 */
public final class WeakRef<T> implements Ref<T> {
	
	private final WeakReference<T> reference;
	
	/**
	 * Constructor.
	 * @param referent the referent.
	 */
	public WeakRef(T referent) {
		this.reference = new WeakReference<T>(referent);
	}

	/*
	 * (non-Javadoc)
	 * @see de.ls5.misc.util.ref.Ref#get()
	 */
	@Override
	public T get() {
		return reference.get();
	}

}