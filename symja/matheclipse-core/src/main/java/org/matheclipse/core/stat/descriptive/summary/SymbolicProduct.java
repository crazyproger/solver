/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.core.stat.descriptive.summary;

import java.io.Serializable;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.stat.descriptive.AbstractSymbolicStorelessUnivariateStatistic;

/**
 * Returns the product of the available values.
 * <p>
 * If there are no values in the dataset, then <code>null</code> is returned.
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.
 * </p>
 * 
 */
public class SymbolicProduct extends AbstractSymbolicStorelessUnivariateStatistic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480199540073305051L;

	/** The number of values that have been added */
	private long n;

	/**
	 * The current Running Product.
	 */
	private IExpr value;

	/**
	 * Create a Product instance
	 */
	public SymbolicProduct() {
		n = 0;
		value = F.C1;
	}

	/**
	 * Copy constructor, creates a new {@code Product} identical to the {@code
	 * original}
	 * 
	 * @param original
	 *          the {@code Product} instance to copy
	 */
	// public Product(Product original) {
	// copy(original, this);
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void increment(final IExpr d) {
		value = F.eval(F.Times(value, d));
		n++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr getResult() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getN() {
		return n;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		value = F.C1;
		n = 0;
	}

	/**
	 * Returns the product of the entries in the specified portion of the input
	 * array, or <code>Double.NaN</code> if the designated subarray is empty.
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if the array is null.
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param begin
	 *          index of the first array element to include
	 * @param length
	 *          the number of elements to include
	 * @return the product of the values or 1 if length = 0
	 * @throws IllegalArgumentException
	 *           if the array is null or the array index parameters are not valid
	 */
	@Override
	public IExpr evaluate(final IAST values, final int begin, final int length) {

		if (test(values, begin, length, true)) {
			IAST product = F.Times();
			// product= 1.0;
			for (int i = begin; i < begin + length; i++) {
				product.add(values.get(i));
			}
			return product;
		}
		return null;
	}

	/**
	 * <p>
	 * Returns the weighted product of the entries in the specified portion of the
	 * input array, or <code>Double.NaN</code> if the designated subarray is
	 * empty.
	 * </p>
	 * 
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if any of the following are
	 * true:
	 * <ul>
	 * <li>the values array is null</li>
	 * <li>the weights array is null</li>
	 * <li>the weights array does not have the same length as the values array</li>
	 * <li>the weights array contains one or more infinite values</li>
	 * <li>the weights array contains one or more NaN values</li>
	 * <li>the weights array contains negative values</li>
	 * <li>the start and length arguments do not determine a valid array</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * Uses the formula,
	 * 
	 * <pre>
	 *    weighted product = &prod;values[i]<sup>weights[i]</sup>
	 * </pre>
	 * 
	 * that is, the weights are applied as exponents when computing the weighted
	 * product.
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param weights
	 *          the weights array
	 * @param begin
	 *          index of the first array element to include
	 * @param length
	 *          the number of elements to include
	 * @return the product of the values or 1 if length = 0
	 * @throws IllegalArgumentException
	 *           if the parameters are not valid
	 * @since 2.1
	 */
	public IExpr evaluate(final IAST values, final IAST weights, final int begin, final int length) {

		if (test(values, weights, begin, length, true)) {
			IAST product = F.Times();
			for (int i = begin; i < begin + length; i++) {
				product.add(F.Power(values.get(i), weights.get(i)));
			}
			if (product.size() > 1) {
				return F.eval(product);
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Returns the weighted product of the entries in the input array.
	 * </p>
	 * 
	 * <p>
	 * Throws <code>IllegalArgumentException</code> if any of the following are
	 * true:
	 * <ul>
	 * <li>the values array is null</li>
	 * <li>the weights array is null</li>
	 * <li>the weights array does not have the same length as the values array</li>
	 * <li>the weights array contains one or more infinite values</li>
	 * <li>the weights array contains one or more NaN values</li>
	 * <li>the weights array contains negative values</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * Uses the formula,
	 * 
	 * <pre>
	 *    weighted product = &prod;values[i]<sup>weights[i]</sup>
	 * </pre>
	 * 
	 * that is, the weights are applied as exponents when computing the weighted
	 * product.
	 * </p>
	 * 
	 * @param values
	 *          the input array
	 * @param weights
	 *          the weights array
	 * @return the product of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *           if the parameters are not valid
	 * @since 2.1
	 */
	public IExpr evaluate(final IAST values, final IAST weights) {
		return evaluate(values, weights, 1, values.size() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	// @Override
	// public Product copy() {
	// Product result = new Product();
	// copy(this, result);
	// return result;
	// }

	/**
	 * Copies source to dest.
	 * <p>
	 * Neither source nor dest can be null.
	 * </p>
	 * 
	 * @param source
	 *          Product to copy
	 * @param dest
	 *          Product to copy to
	 * @throws NullArgumentException
	 *           if either source or dest is null
	 */
	// public static void copy(Product source, Product dest) throws
	// NullArgumentException {
	// MathUtils.checkNotNull(source);
	// MathUtils.checkNotNull(dest);
	// dest.setData(source.getDataRef());
	// dest.n = source.n;
	// dest.value = source.value;
	// }

}
