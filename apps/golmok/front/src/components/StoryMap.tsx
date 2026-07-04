"use client";

import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import Link from "next/link";
import Image from "next/image";
import { resolveAssetUrl } from "@/lib/api-client";
import type { MapMarker } from "@/types";

const defaultIcon = L.icon({
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

L.Marker.prototype.options.icon = defaultIcon;

type StoryMapProps = {
  markers: MapMarker[];
  onLocationSelect?: (lat: number, lng: number) => void;
  selectable?: boolean;
  center?: [number, number];
  zoom?: number;
};

function LocationPicker({
  onSelect,
}: {
  onSelect: (lat: number, lng: number) => void;
}) {
  useMapEvents({
    click(e) {
      onSelect(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}

export function StoryMap({
  markers,
  onLocationSelect,
  selectable = false,
  center = [35.8242, 127.148],
  zoom = 14,
}: StoryMapProps) {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <div className="flex h-[500px] items-center justify-center rounded-xl bg-cream-dark">
        <p className="text-sepia">지도를 불러오는 중...</p>
      </div>
    );
  }

  return (
    <MapContainer
      center={center}
      zoom={zoom}
      className="h-[500px] w-full rounded-xl"
      scrollWheelZoom={true}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {selectable && onLocationSelect && (
        <LocationPicker onSelect={onLocationSelect} />
      )}
      {markers.map((marker) => (
        <Marker key={marker.storyId} position={[marker.latitude, marker.longitude]}>
          <Popup>
            <div className="w-48">
              <div className="relative mb-2 h-24 w-full overflow-hidden rounded">
                <Image
                  src={resolveAssetUrl(marker.thumbnailUrl)}
                  alt={marker.title}
                  fill
                  className="object-cover"
                />
              </div>
              <p className="font-serif font-semibold text-sm">{marker.title}</p>
              <span className="tag-pill mt-1">{marker.category}</span>
              <Link
                href={`/stories/${marker.storyId}`}
                className="mt-2 block text-center text-sm text-rust hover:underline"
              >
                자세히 보기 →
              </Link>
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}
