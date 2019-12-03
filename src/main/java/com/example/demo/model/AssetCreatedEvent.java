package com.example.demo.model;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.Getter;

@Data
public class AssetCreatedEvent extends ApplicationEvent {

	private int assetId;

	public AssetCreatedEvent(Object source, int assetId) {
		super(source);
		this.assetId = assetId;
	}

}
