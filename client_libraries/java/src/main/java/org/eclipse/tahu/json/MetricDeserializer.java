/*******************************************************************************
 * Copyright (c) 2014, 2018 Cirrus Link Solutions and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *  Cirrus Link Solutions
 *
 *******************************************************************************/

package org.eclipse.tahu.json;

import java.io.IOException;
import java.util.Base64;

import org.eclipse.tahu.message.model.File;
import org.eclipse.tahu.message.model.MetaData;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.MetricDataType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * A custom JSON deserializer for {@link Metric} instances.
 */
public class MetricDeserializer extends StdDeserializer<Metric> implements ResolvableDeserializer {

	private final JsonDeserializer<?> defaultDeserializer;

	/**
	 * Constructor.
	 */
	protected MetricDeserializer(JsonDeserializer<?> defaultDeserializer) {
		super(Metric.class);
		this.defaultDeserializer = defaultDeserializer;
	}

	@Override
	public Metric deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		Metric metric = (Metric) defaultDeserializer.deserialize(parser, ctxt);
		System.out.println(metric);
		
		// Check if the data type is a File
		if (metric.getDataType().equals(MetricDataType.File)) {
			// Perform the custom logic for File types by building up the File object.
			MetaData metaData = metric.getMetaData();
			String fileName = metaData == null ? null : metaData.getFileName();
			File file = new File(fileName, Base64.getDecoder().decode((String)metric.getValue()));
			metric.setValue(file);
		}
		return metric;
	}

	@Override
	public void resolve(DeserializationContext ctxt) throws JsonMappingException {
		((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
	}

}