package com.example.demo.ignite;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.cluster.DetachedClusterNode;
import org.apache.ignite.lang.IgniteProductVersion;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.DiscoveryMetricsProvider;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.DiscoverySpiCustomMessage;
import org.apache.ignite.spi.discovery.DiscoverySpiDataExchange;
import org.apache.ignite.spi.discovery.DiscoverySpiListener;
import org.apache.ignite.spi.discovery.DiscoverySpiNodeAuthenticator;
import org.jetbrains.annotations.Nullable;

public class StandaloneDiscoverySpi extends IgniteSpiAdapter implements DiscoverySpi {
	private final Serializable consistentId = UUID.randomUUID().toString();
	private final ClusterNode localNode;

	public StandaloneDiscoverySpi() {
		localNode = new DetachedClusterNode(consistentId, new HashMap<>());
	}

	/** {@inheritDoc} */
	@Nullable
	@Override public Serializable consistentId() throws IgniteSpiException {
		return consistentId;
	}

	/** {@inheritDoc} */
	@Override public Collection<ClusterNode> getRemoteNodes() {
		return null;
	}

	/** {@inheritDoc} */
	@Override public ClusterNode getLocalNode() {
		return localNode;
	}

	/** {@inheritDoc} */
	@Nullable @Override public ClusterNode getNode(UUID nodeId) {
		return localNode;
	}

	/** {@inheritDoc} */
	@Override public boolean pingNode(UUID nodeId) {
		return true;
	}

	/** {@inheritDoc} */
	@Override public void setNodeAttributes(Map<String, Object> attrs, IgniteProductVersion ver) {
	}

	/** {@inheritDoc} */
	@Override public void setListener(@Nullable DiscoverySpiListener lsnr) {
	}

	/** {@inheritDoc} */
	@Override public void setDataExchange(DiscoverySpiDataExchange exchange) {
	}

	/** {@inheritDoc} */
	@Override public void setMetricsProvider(DiscoveryMetricsProvider metricsProvider) {
	}

	/** {@inheritDoc} */
	@Override public void disconnect() throws IgniteSpiException {
	}

	/** {@inheritDoc} */
	@Override public void setAuthenticator(DiscoverySpiNodeAuthenticator auth) {
	}

	/** {@inheritDoc} */
	@Override public long getGridStartTime() {
		return 0;
	}

	/** {@inheritDoc} */
	@Override public void sendCustomEvent(DiscoverySpiCustomMessage msg) throws IgniteException {
	}

	/** {@inheritDoc} */
	@Override public void failNode(UUID nodeId, @Nullable String warning) {
	}

	/** {@inheritDoc} */
	@Override public boolean isClientMode() throws IllegalStateException {
		return false;
	}

	/** {@inheritDoc} */
	@Override public void spiStart(@Nullable String igniteInstanceName) throws IgniteSpiException {
	}

	/** {@inheritDoc} */
	@Override public void spiStop() throws IgniteSpiException {
	}
}
