import type { ReactNode } from "react";

import "./globals.css";
import { Providers } from "./providers";

export const metadata = {
  title: "IF Web 2",
  description: "고령자 위험도 판단 시스템",
};

type RootLayoutProps = {
  children: ReactNode;
};

export default function RootLayout({ children }: RootLayoutProps) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
