package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.model.AssetCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AssetCache {

	private List<AssetCreatedEvent> events = new ArrayList<>();

	@EventListener(AssetCreatedEvent.class)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onAssetCreated(AssetCreatedEvent assetCreatedEvent) {
		log.info("AssetCreated: {}", assetCreatedEvent.getAssetId());
		events.add(assetCreatedEvent);
	}

	public void clear() {
		events.clear();
	}

	public List<AssetCreatedEvent> getEvents() {
		return events;
	}
}
