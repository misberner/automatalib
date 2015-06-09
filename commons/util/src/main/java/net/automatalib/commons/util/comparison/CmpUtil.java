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
package net.automatalib.commons.util.comparison;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Various methods for dealing with the comparison of objects.
 * 
 * @author Malte Isberner
 *
 */
public abstract class CmpUtil {
	
	/**
	 * Enum for controlling which rank is assigned to a <code>null</code>
	 * element when using a safe comparator
	 * ({@link CmpUtil#safeComparator(Comparator, NullOrdering)}).
	 * 
	 * @author Malte Isberner
	 *
	 */
	public static enum NullOrdering {
		/**
		 * <code>null</code> elements are smaller than all regular elements.
		 */
		MIN(-1),
		/**
		 * <code>null</code> elements are bigger than all regular elements.
		 */
		MAX(1);
		
		/**
		 * Value that determines the result of the comparison, when
		 * only the first value is a <code>null</code> value.
		 */
		public final int firstNullResult;
		
		private NullOrdering(int firstNullResult) {
			this.firstNullResult = firstNullResult;
		}
	}

	
	/*
	 * Prevent inheritance.
	 */
	private CmpUtil() {
	}
	
	
	/**
	 * Lexicographically compares two {@link Iterable}s. Comparison of the
	 * elements is done using the specified comparator.
	 * 
	 * @param o1 the first iterable.
	 * @param o2 the second iterable.
	 * @param elemComparator the comparator.
	 * @return <code>&lt; 0</code> iff o1 is lexicographically smaller,
	 * <code>0</code> if o1 equals o2 and <code>&gt; 0</code> otherwise.
	 */
	public static <U> int lexCompare(Iterable<? extends U> o1, Iterable<? extends U> o2, Comparator<U> elemComparator) {
		Iterator<? extends U> it1 = o1.iterator(), it2 = o2.iterator();
		
		while(it1.hasNext() && it2.hasNext()) {
			int cmp = elemComparator.compare(it1.next(), it2.next());
			if(cmp != 0)
				return cmp;
		}
		
		if(it1.hasNext())
			return 1;
		else if(it2.hasNext())
			return -1;
		return 0;
	}
	
	public static int lexCompare(int[] a1, int[] a2) {
		int i = 0;
		int len1 = a1.length, len2 = a2.length;
		
		while (i < len1 && i < len2) {
			int cmp = a1[i] - a2[i];
			if (cmp != 0) {
				return cmp;
			}
			i++;
		}
		
		if (i < len1) {
			return 1;
		}
		if (i < len2) {
			return -1;
		}
		return 0;
	}
	
	public static int canonicalCompare(int[] a1, int [] a2) {
		int ldiff = a1.length - a2.length;
		if (ldiff != 0) {
			return ldiff;
		}
		return lexCompare(a1, a2);
	}
	
	/**
	 * Lexicographically compares two {@link Iterable}s, whose element types
	 * are comparable.
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static <U extends Comparable<? super U>> int lexCompare(Iterable<? extends U> o1, Iterable<? extends U> o2) {
		Iterator<? extends U> it1 = o1.iterator(), it2 = o2.iterator();
		
		while(it1.hasNext() && it2.hasNext()) {
			int cmp = it1.next().compareTo(it2.next());
			if(cmp != 0)
				return cmp;
		}
		
		if(it1.hasNext())
			return 1;
		else if(it2.hasNext())
			return -1;
		return 0;
	}
	
	/**
	 * Compares two {@link List}s with respect to canonical ordering.
	 * <p>
	 * In canonical ordering, a sequence <tt>o1</tt> is less than a sequence <tt>o2</tt> if <tt>o1</tt> is shorter
	 * than <tt>o2</tt>, or if they have the same length and <tt>o1</tt> is lexicographically smaller than <tt>o2</tt>. 
	 * @param o1 the first list
	 * @param o2 the second list
	 * @param elemComparator the comparator for comparing the single elements
	 * @return the result of the comparison
	 */
	public static <U> int canonicalCompare(List<? extends U> o1, List<? extends U> o2, Comparator<? super U> elemComparator) {
		int siz1 = o1.size(), siz2 = o2.size();
		
		if(siz1 != siz2) {
			return siz1 - siz2;
		}
		
		return lexCompare(o1, o2, elemComparator);
	}
	
	/**
	 * Compares two {@link List}s of {@link Comparable} elements with respect to canonical ordering.
	 * <p>
	 * In canonical ordering, a sequence <tt>o1</tt> is less than a sequence <tt>o2</tt> if <tt>o1</tt> is shorter
	 * than <tt>o2</tt>, or if they have the same length and <tt>o1</tt> is lexicographically smaller than <tt>o2</tt>.
	 * @param o1 the first list
	 * @param o2 the second list
	 * @return the result of the comparison
	 */
	public static <U extends Comparable<? super U>> int canonicalCompare(List<? extends U> o1, List<? extends U> o2) {
		int siz1 = o1.size(), siz2 = o2.size();
		if(siz1 != siz2) {
			return siz1 - siz2;
		}
		
		return lexCompare(o1, o2);
	}
	
	/**
	 * Retrieves a lexicographical comparator for the given type.
	 * @param elemComp the comparator to use for comparing the elements.
	 * @return a comparator for comparing objects of type <code>T</code>
	 * based on lexicographical ordering.
	 */
	public static <T extends Iterable<U>,U> Comparator<T> lexComparator(Comparator<U> elemComp) {
		return new LexComparator<T,U>(elemComp);
	}
	
	/**
	 * Retrieves a lexicographical comparator for the given type, which has
	 * to be an {@link Iterable} of {@link Comparable} types.
	 * @return the lexicographical comparator.
	 */
	public static <U extends Comparable<U>,T extends Iterable<U>> Comparator<T> lexComparator() {
		return NaturalLexComparator.<T,U>getInstance();
	}
	
	
	/**
	 * Retrieves a canonical comparator for the given list type.
	 * @param elemComp the comparator to use for comparing the elements.
	 * @return a comparator for comparing objects of type <code>T</code> based on
	 * canonical ordering.
	 */
	public static <T extends List<? extends U>,U> Comparator<T> canonicalComparator(Comparator<? super U> elemComp) {
		return new CanonicalComparator<>(elemComp);
	}
	
	/**
	 * Retrieves a canonical comparator for the given type, which has to be
	 * a {@link List} of {@link Comparable} types.
	 * @return the canonical comparator
	 * @see #canonicalCompare(List, List)
	 */
	public static <T extends List<U>,U extends Comparable<U>> Comparator<T> canonicalComparator() {
		return NaturalCanonicalComparator.<T,U>getInstance();
	}
	
	/**
	 * Retrieves a <i>safe</i> comparator, which can handle <code>null</code>
	 * element values.
	 * Whether <code>null</code> values are smaller or bigger than regular
	 * values is controlled by the {@link NullOrdering} parameter.
	 * @param <T> original element class.
	 * @param baseComp the basic comparator.
	 * @param nullOrd the ordering policy for <code>null</code> values.
	 * @return a safe comparator using the specified underlying comparator.
	 */
	public static <T> Comparator<T> safeComparator(Comparator<T> baseComp, NullOrdering nullOrd) {
		return new SafeComparator<T>(baseComp, nullOrd);
	}
	
	
	
	/**
	 * Retrieves a {@link Comparator} that compares elements according to their
	 * natural ordering (i.e., they have to implement the {@link Comparable}
	 * interface.
	 * 
	 * If this comparator is used on elements that don't implement this
	 * interface, this may result in a {@link ClassCastException}.
	 * 
	 * @param <T> element class.
	 * @return the natural ordering comparator.
	 */
	public static <T extends Comparable<T>> Comparator<T> naturalOrderingComparator() {
		return NaturalOrderingComparator.getInstance();
	}
}