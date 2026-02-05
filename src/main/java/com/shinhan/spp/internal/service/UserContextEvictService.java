package com.shinhan.spp.internal.service;

import com.shinhan.spp.advice.UserContextCache;
import com.shinhan.spp.internal.dto.UserContextEvictReq;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.util.*;

@Component
public class UserContextEvictService {

    private final RestTemplate internalRestTemplate;
    private final List<String> nodes;
    private final String internalToken;
    private final String evictUri;

    public UserContextEvictService(
            @Qualifier("internalRestTemplate") RestTemplate internalRestTemplate,
            @Value("${app.internal.nodes:}") String[] nodes,
            @Value("${app.internal.token:}") String internalToken,
            @Value("${app.internal.evict-uri:}") String evictUri
    ) {
        this.internalRestTemplate = internalRestTemplate;
        this.nodes = normalizeNodes(nodes);
        this.internalToken = internalToken;
        this.evictUri = (evictUri == null ? "" : evictUri.trim());
    }


    /**
     * 트랜잭션 커밋 이후에만 내 노드 + 상대 노드 캐시 무효화를 수행
     * @param userId 사번
     */
    public void evictAfterCommit(String userId) {
        if (isBlank(userId)) return;

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            evictNow(userId);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                evictNow(userId);
            }
        });
    }

    /**
     * 즉시 내 노드 + 상대 노드 캐시를 무효화한다.
     * @param userId 사번
     */
    public void evictNow(String userId) {
        if (isBlank(userId)) return;

        // 내 노드 캐시 제거
        UserContextCache.evict(userId);

        // 상대 노드 캐시 제거 (실패시 무시)
        try {
            evictPeer(userId);
        } catch (Exception ignore) {
            // ignore
        }
    }

    /**
     * 상대 노드(UserContextCache)만 evict 한다.
     *
     * - 내 노드를 판별하지 못하면(nodes와 로컬 NIC IP가 매칭되지 않으면) 호출하지 않는다.
     * - 실패(타임아웃/일시 장애)는 예외를 던진다(상위에서 무시 가능).
     */
    public void evictPeer(String userId) {
        if (isBlank(userId)) return;

        String peerBaseUrl = resolvePeerBaseUrl();
        if (peerBaseUrl == null) return;

        String url = peerBaseUrl + buildEvictPath();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!isBlank(internalToken)) {
            headers.set("X-Internal-Token", internalToken);
        }

        UserContextEvictReq body = new UserContextEvictReq(userId, UUID.randomUUID().toString());
        internalRestTemplate.postForEntity(URI.create(url), new HttpEntity<>(body, headers), String.class);
    }

    private String buildEvictPath() {
        String p = evictUri;
        if (isBlank(p)) p = "/internal/user-context/evict";

        // 앞 슬래시 보정
        if (!p.startsWith("/")) p = "/" + p;
        return p;
    }

    /**
     * nodes 중 로컬 IPv4와 host가 일치하는 것을 self로 보고, 나머지 하나를 peer로 반환
     */
    private String resolvePeerBaseUrl() {
        if (nodes == null || nodes.size() < 2) return null;

        Set<String> localIps = getLocalIpv4s();
        if (localIps.isEmpty()) return null;

        String self = null;

        for (String n : nodes) {
            try {
                URI uri = URI.create(n);
                String host = uri.getHost();
                if (!isBlank(host) && localIps.contains(host)) {
                    self = trimSlash(n);
                    break;
                }
            } catch (Exception ignore) {
                // ignore
            }
        }

        if (self == null) return null;

        for (String n : nodes) {
            String nn = trimSlash(n);
            if (!nn.equals(self)) return nn;
        }
        return null;
    }

    private static List<String> normalizeNodes(String[] nodes) {
        if (nodes == null) return List.of();

        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String n : nodes) {
            if (n == null) continue;
            String s = trimSlash(n.trim());
            if (s.isEmpty()) continue;
            set.add(s);
        }
        return new ArrayList<>(set);
    }

    private static Set<String> getLocalIpv4s() {
        try {
            Set<String> ips = new HashSet<>();
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

            while (ifaces.hasMoreElements()) {
                NetworkInterface ni = ifaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;

                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (a instanceof Inet4Address) {
                        String ip = a.getHostAddress();
                        if (!isBlank(ip)) ips.add(ip);
                    }
                }
            }
            return ips;
        } catch (Exception e) {
            return Set.of();
        }
    }

    private static String trimSlash(String url) {
        if (url == null) return "";
        String s = url.trim();
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
