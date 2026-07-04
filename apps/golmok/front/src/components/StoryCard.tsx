import Image from "next/image";
import { resolveAssetUrl, storyDetailHref } from "@/lib/api-client";

type StoryCardProps = {
  id: string;
  title: string;
  description?: string;
  imageUrl: string;
  category: string;
  periodTag?: string | null;
  locationName?: string;
  nickname?: string;
  likeCount?: number;
};

export function StoryCard({
  id,
  title,
  description,
  imageUrl,
  category,
  periodTag,
  locationName,
  nickname,
  likeCount,
}: StoryCardProps) {
  return (
    <a href={storyDetailHref(id)} className="group block">
      <article className="vintage-card overflow-hidden rounded-xl transition-all duration-300">
        <div className="relative aspect-[4/3] overflow-hidden">
          <Image
            src={resolveAssetUrl(imageUrl)}
            alt={title}
            fill
            className="object-cover transition-transform duration-500 group-hover:scale-105"
            sizes="(max-width: 768px) 100vw, 33vw"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-ink/60 via-transparent to-transparent" />
          <div className="absolute bottom-3 left-3 flex gap-2">
            <span className="tag-pill bg-white/90">{category}</span>
            {periodTag && (
              <span className="tag-pill bg-rust/90 text-white">{periodTag}</span>
            )}
          </div>
        </div>
        <div className="p-4">
          <h3 className="font-serif text-lg font-semibold text-ink line-clamp-1 group-hover:text-rust transition-colors">
            {title}
          </h3>
          {description && (
            <p className="mt-1 text-sm text-sepia line-clamp-2">{description}</p>
          )}
          <div className="mt-3 flex items-center justify-between text-xs text-sepia">
            {locationName && <span>{locationName}</span>}
            <div className="flex items-center gap-3">
              {nickname && <span>by {nickname}</span>}
              {likeCount !== undefined && likeCount > 0 && (
                <span>♥ {likeCount}</span>
              )}
            </div>
          </div>
        </div>
      </article>
    </a>
  );
}
