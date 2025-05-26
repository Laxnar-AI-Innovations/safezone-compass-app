
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.laxnar.hersafezone',
  appName: 'HERSAFEZONE',
  webDir: 'dist',
  server: {
    url: "https://84dba154-4a6d-4715-aa4c-f72b46421122.lovableproject.com?forceHideBadge=true",
    cleartext: true
  },
  android: {
    minWebViewVersion: 60
  }
};

export default config;
