import StoryDetailClient from "./StoryDetailClient";

// static export는 빌드 시점에 알 수 없는 동적 storyId 페이지를 생성할 수 없으므로
// placeholder 경로 하나만 생성하고, nginx가 /golmok/stories/* 요청을 전부 이
// 정적 shell로 서빙한다. 실제 storyId는 클라이언트에서 useParams()로 읽는다.
export async function generateStaticParams() {
  return [{ storyId: "_" }];
}

export default function StoryDetailPage() {
  return <StoryDetailClient />;
}
