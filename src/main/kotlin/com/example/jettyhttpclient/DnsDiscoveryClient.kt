package com.example.jettyhttpclient

import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.stereotype.Component
import java.net.Inet4Address
import java.net.InetAddress

/**
 * A custom DNS-based service discovery client.
 * It uses DNS lookups to discover service instances and provides reactive support for querying these instances.
 */
@Component
class DnsDiscoveryClient : DiscoveryClient {

    override fun description() = "DNS Discovery Client"

    /**
     * Retrieves a list of service instances for a given service name using DNS lookup.
     *
     * @param serviceId The hostname of the service to discover.
     * @return ServiceInstance objects representing the discovered service instances.
     */
    override fun getInstances(serviceId: String): List<ServiceInstance> =
        lookup(serviceId)
            .map {
                DefaultServiceInstance(
                    it.hostAddress,
                    serviceId,
                    it.hostAddress,
                    port(),
                    false,
                )
            }

    /**
     * Returns all hubs host names that are configured in the application.yaml
     */
    override fun getServices() = listOf("localhost")

    /**
     * Performs a DNS lookup for the given hostname and filters for IPv4 addresses.
     */
    private fun lookup(hostname: String) = InetAddress.getAllByName(hostname).filterIsInstance<Inet4Address>().toList()

    private fun port() = 8080
}
