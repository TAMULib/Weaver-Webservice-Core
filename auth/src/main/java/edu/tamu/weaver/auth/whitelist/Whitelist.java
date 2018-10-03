package edu.tamu.weaver.auth.whitelist;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("whitelist")
public class Whitelist {

    private static final Logger logger = LoggerFactory.getLogger(Whitelist.class);

    private static final String HEADER_X_REAL_IP = "x-real-ip";

    private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";

    @Value("${app.whitelist:127.0.0.1}")
    private String[] whitelist;

    @PostConstruct
    public void info() {
        logger.debug("Component whitelist initialized");
    }

    public boolean isAllowed(HttpServletRequest req) throws UnknownHostException {
        String ip = req.getRemoteAddr();
        logger.debug("Request ip: " + ip);

        String realIp = req.getHeader(HEADER_X_REAL_IP);
        logger.debug("Request real ip: " + realIp);

        String forwardForIps = req.getHeader(HEADER_X_FORWARDED_FOR);
        logger.debug("Request forwarded for ip: " + forwardForIps);

        boolean allowed = false;

        for (String entry : whitelist) {
            if (ip != null && entry.trim().equals(ip.trim())) {
                logger.debug("Allowing whitelist ip " + entry + " for " + req.getRequestURI());
                allowed = true;
                break;
            }
            if (realIp != null && entry.trim().equals(realIp.trim())) {
                logger.debug("Allowing whitelist real ip " + realIp + " for " + req.getRequestURI());
                allowed = true;
                break;
            }
            if (forwardForIps != null && forwardForIps.contains(entry.trim())) {
                logger.debug("Allowing whitelist forwarded for ips " + forwardForIps + " for " + req.getRequestURI());
                allowed = true;
                break;
            }
        }

        String realHost = null;
        String forwardForHosts = null;

        if (!allowed) {
            if (realIp != null) {
                realHost = getHostFromIp(realIp);
                if (realHost != null) {
                    logger.debug("Request real host: " + realHost);
                }
            }

            for (String entry : whitelist) {
                if (realHost != null && entry.trim().equals(realHost.trim())) {
                    logger.debug("Allowing whitelist real host " + realHost + " for " + req.getRequestURI());
                    allowed = true;
                    break;
                }
            }
        }

        if (!allowed) {
            if (forwardForIps != null) {
                forwardForHosts = getForwardedForHosts(forwardForIps);
            }

            for (String entry : whitelist) {
                if (forwardForHosts != null && forwardForHosts.contains(entry.trim())) {
                    logger.debug("Allowing whitelist forwarded for hosts " + forwardForHosts + " for " + req.getRequestURI());
                    allowed = true;
                    break;
                }
            }
        }

        if (!allowed) {
            logger.warn("Disallowing request for " + req.getRequestURI());
            if (ip != null) {
                logger.warn("  ip: " + ip);
            }
            if (realIp != null) {
                logger.warn("  real ip: " + realIp);
            }
            if (forwardForIps != null) {
                logger.warn("  forwarded for IPs: " + forwardForIps);
            }
            if (realHost != null) {
                logger.warn("  real host: " + realHost);
            }
            if (forwardForIps != null) {
                logger.warn("  forwarded for IPs: " + forwardForIps);
            }
        }
        return allowed;
    }

    private String getHostFromIp(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            return addr.getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Unknown host for ip: " + ip);
        }
        return null;
    }

    private String getForwardedForHosts(String forwardForIps) {
        String forwardForHosts = "";
        Iterator<String> forwardForIpsIterator = Arrays.stream(forwardForIps.split(",")).iterator();
        while (forwardForIpsIterator.hasNext()) {
            String forwardForHost = getHostFromIp(forwardForIpsIterator.next());
            if (forwardForHost != null) {
                forwardForHosts += forwardForHost;
                if (forwardForIpsIterator.hasNext()) {
                    forwardForHosts += ",";
                }
            }
        }
        if (forwardForHosts.isEmpty()) {
            return null;
        } else {
            logger.info("Request forwarded for hosts: " + forwardForHosts);
            return forwardForHosts;
        }
    }

}
