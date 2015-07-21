/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Dave Syer
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@Document
public class ServiceLocation {

	@Id
	private Long id;
	private String address1;
	private String address2;
	private String city;
	@JsonIgnore
	private final @GeoSpatialIndexed Point point;
	private String location;
	private String state;
	private String zip;
	private String type;

	@SuppressWarnings("unused")
	private ServiceLocation() {
		this.point = new Point(0, 0);
	}

	@PersistenceConstructor
	@JsonCreator
	public ServiceLocation(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
		this.point = new Point(longitude, latitude);
	}

	public double getLatitude() {
		return this.point.getY();
	}

	public double getLongitude() {
		return this.point.getX();
	}

}
