package com.cloud.binding;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ResourceServer extends ReactiveApiBinding {
	private String resourceServerUrl;

	public ResourceServer(String accessToken) {
		super(accessToken);
	}
}
