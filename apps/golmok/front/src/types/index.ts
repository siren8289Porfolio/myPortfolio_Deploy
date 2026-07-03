export type StoryWithRelations = {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  thumbnailUrl?: string;
  images?: { imageUrl: string; thumbnailUrl: string | null }[];
  status: string;
  periodTag: string | null;
  category: string;
  sourceType: string | null;
  likeCount?: number;
  createdAt: string;
  createdBy: { id: string; nickname: string };
  place: {
    id: string;
    locationName: string;
    latitude: number;
    longitude: number;
    region: { id: string; name: string };
  };
  tags: { tag: { id: string; name: string; type: string } }[];
  _count?: { reactions: number };
};

export type MapMarker = {
  storyId: string;
  title: string;
  latitude: number;
  longitude: number;
  thumbnailUrl: string;
  category: string;
  locationName?: string;
};

export type ApiResponse<T> = {
  success: boolean;
  data: T;
  message: string;
};

export type ApiErrorResponse = {
  success: false;
  errorCode: string;
  message: string;
};
