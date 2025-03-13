package com.pinup.domain.place.service;

import com.pinup.domain.place.dto.request.MapViewDto;
import com.pinup.domain.place.dto.response.MapPlaceResponse;
import com.pinup.domain.place.dto.response.PlaceClusterDto;
import com.pinup.domain.place.dto.response.TotalPlaceResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaceClusteringService {

    // 클러스터링을 위한 거리 임계값 (미터 단위)
    private static final double BASE_CLUSTERING_DISTANCE = 100.0;

    // 줌 레벨별 클러스터링 거리 조정 계수
    private static final Map<Integer, Double> ZOOM_CLUSTERING_FACTORS = new HashMap<>();
    private static final int EARTH_RADIUS_KM = 6371;

    static {
        // 줌 레벨별 클러스터링 거리 조정 계수 설정
        // 숫자가 작을수록 거리가 커짐 (더 많은 장소가 클러스터링됨)
        ZOOM_CLUSTERING_FACTORS.put(22, 0.05);  // 가장 확대 (건물 수준)
        ZOOM_CLUSTERING_FACTORS.put(21, 0.1);
        ZOOM_CLUSTERING_FACTORS.put(20, 0.2);
        ZOOM_CLUSTERING_FACTORS.put(19, 0.3);
        ZOOM_CLUSTERING_FACTORS.put(18, 0.5);   // 거리 수준
        ZOOM_CLUSTERING_FACTORS.put(17, 0.8);
        ZOOM_CLUSTERING_FACTORS.put(16, 1.0);   // 기본 거리
        ZOOM_CLUSTERING_FACTORS.put(15, 1.5);
        ZOOM_CLUSTERING_FACTORS.put(14, 2.0);   // 동네 수준
        ZOOM_CLUSTERING_FACTORS.put(13, 3.0);
        ZOOM_CLUSTERING_FACTORS.put(12, 5.0);   // 지구/구 수준
        ZOOM_CLUSTERING_FACTORS.put(11, 8.0);
        ZOOM_CLUSTERING_FACTORS.put(10, 12.0);  // 도시 수준
        ZOOM_CLUSTERING_FACTORS.put(9, 20.0);
        ZOOM_CLUSTERING_FACTORS.put(8, 30.0);   // 광역 수준
        ZOOM_CLUSTERING_FACTORS.put(7, 50.0);
        ZOOM_CLUSTERING_FACTORS.put(6, 80.0);   // 광역도/주 수준
        ZOOM_CLUSTERING_FACTORS.put(5, 120.0);
        ZOOM_CLUSTERING_FACTORS.put(4, 200.0);  // 국가 수준
        ZOOM_CLUSTERING_FACTORS.put(3, 300.0);
        ZOOM_CLUSTERING_FACTORS.put(2, 500.0);  // 대륙 수준
        ZOOM_CLUSTERING_FACTORS.put(1, 1000.0); // 가장 축소
    }

    /**
     * 줌 레벨에 따라 장소 리스트를 클러스터링하여 반환
     * @param places 클러스터링할 장소 리스트
     * @param mapView 지도 뷰 정보 (중심 좌표, 경계, 줌 레벨)
     * @return 클러스터링된 결과
     */
    public TotalPlaceResponse clusterPlacesByZoomLevel(List<MapPlaceResponse> places, MapViewDto mapView) {
        if (places.isEmpty()) {
            return null;
        }

        // 줌 레벨에 따른 클러스터링 거리 계산
        double clusteringDistance = calculateClusteringDistance(mapView.getZoomLevel());

        // 장소를 지도 중심으로부터의 거리에 따라 정렬
        List<MapPlaceResponse> sortedPlaces = sortPlacesByDistanceFromCenter(places, mapView.getCenterLat(), mapView.getCenterLng());

        // 클러스터링 수행
        return createClusters(sortedPlaces, clusteringDistance, mapView);
    }

    /**
     * 정렬된 장소 목록에서 클러스터를 생성
     */
    private TotalPlaceResponse createClusters(List<MapPlaceResponse> sortedPlaces, double clusteringDistance, MapViewDto mapView) {
        // 클러스터링 결과를 저장할 리스트
        List<PlaceClusterDto> clusters = new ArrayList<>();

        // 클러스터링 되지 않은 단일 장소를 저장할 리스트
        List<MapPlaceResponse> places = new ArrayList<>();

        // 처리된 장소의 kakaoPlaceId를 추적하기 위한 Set
        Set<String> processedPlaceKakaoIds = new HashSet<>();

        for (MapPlaceResponse place : sortedPlaces) {
            // 이미 처리된 장소는 건너뜀
            if (processedPlaceKakaoIds.contains(place.getKakaoPlaceId())) {
                continue;
            }

            // 현재 장소 기준으로 근처 장소 찾기
            List<MapPlaceResponse> nearbyPlaces = findNearbyPlaces(place, sortedPlaces, clusteringDistance, processedPlaceKakaoIds);

            // 처리된 장소 목록에 추가
            processedPlaceKakaoIds.add(place.getKakaoPlaceId());
            if (nearbyPlaces.isEmpty()) {
                places.add(place);
                continue;
            }
            nearbyPlaces.forEach(nearbyPlace -> processedPlaceKakaoIds.add(nearbyPlace.getKakaoPlaceId()));

            // 클러스터 생성 및 추가
            clusters.add(createCluster(place, nearbyPlaces));
        }

        return new TotalPlaceResponse(clusters, places.stream()
                .sorted(Comparator.comparingDouble(place ->
                        calculateDistance(mapView.getCurrLat(), mapView.getCurrLng(), place.getLatitude(), place.getLongitude())))
                .collect(Collectors.toList()));
    }

    private PlaceClusterDto createCluster(MapPlaceResponse centerPlace, List<MapPlaceResponse> nearbyPlaces) {

        // 클러스터에 포함될 모든 장소
        List<MapPlaceResponse> clusterPlaces = new ArrayList<>();
        clusterPlaces.add(centerPlace);
        clusterPlaces.addAll(nearbyPlaces);
        // 클러스터의 중심좌표 계산
        double avgLat = clusterPlaces.stream()
                .mapToDouble(MapPlaceResponse::getLatitude)
                .average()
                .orElse(centerPlace.getLatitude());
        double avgLng = clusterPlaces.stream()
                .mapToDouble(MapPlaceResponse::getLongitude)
                .average()
                .orElse(centerPlace.getLongitude());

        return PlaceClusterDto.builder()
                .clusterId(centerPlace.getKakaoPlaceId())
                .latitude(avgLat)
                .longitude(avgLng)
                .count(clusterPlaces.size())
                .places(clusterPlaces)
                .build();
    }

    private List<MapPlaceResponse> findNearbyPlaces(
            MapPlaceResponse centerPlace, List<MapPlaceResponse> allPlaces,
            double clusteringDistance, Set<String> processedKakaoPlaceIds
    ) {
        List<MapPlaceResponse> nearbyPlaces = new ArrayList<>();
        for (MapPlaceResponse otherPlace : allPlaces) {
            String otherId = otherPlace.getKakaoPlaceId();
            // 자기 자신이거나 이미 처리된 장소는 제외
            if (centerPlace.getKakaoPlaceId().equals(otherId) || processedKakaoPlaceIds.contains(otherId)) {
                continue;
            }
            // 거리 계산하여 클러스터링 거리 이내인 경우 추가
            double distance = calculateDistance(
                    centerPlace.getLatitude(), centerPlace.getLongitude(),
                    otherPlace.getLatitude(), otherPlace.getLongitude()
            );
            if (distance <= clusteringDistance) {
                nearbyPlaces.add(otherPlace);
            }
        }

        return nearbyPlaces;
    }

    private List<MapPlaceResponse> sortPlacesByDistanceFromCenter(List<MapPlaceResponse> places, Double centerLat, Double centerLng) {
        return places.stream()
                .sorted(Comparator.comparingDouble(place ->
                        calculateDistance(centerLat, centerLng, place.getLatitude(), place.getLongitude())))
                .collect(Collectors.toList());
    }

    private double calculateClusteringDistance(int zoomLevel) {
        // 줌 레벨이 맵에 없으면 기본 거리 사용
        double factor = ZOOM_CLUSTERING_FACTORS.getOrDefault(zoomLevel, 1.0);

        return BASE_CLUSTERING_DISTANCE * factor;
    }

    /**
     * 두 지점 간의 거리를 계산 (Haversine 공식 사용)
     * @return 거리 (미터단위)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000; // 미터 단위로 변환
    }
}
