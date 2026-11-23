---
name: Cockpit Broadcast
colors:
  surface: '#0f141a'
  surface-dim: '#0f141a'
  surface-bright: '#353941'
  surface-container-lowest: '#0a0e15'
  surface-container-low: '#181c23'
  surface-container: '#1c2027'
  surface-container-high: '#262a32'
  surface-container-highest: '#31353d'
  on-surface: '#dfe2ec'
  on-surface-variant: '#bbc9cf'
  inverse-surface: '#dfe2ec'
  inverse-on-surface: '#2d3038'
  outline: '#859399'
  outline-variant: '#3c494e'
  surface-tint: '#47d6ff'
  primary: '#a5e7ff'
  on-primary: '#003543'
  primary-container: '#00d2ff'
  on-primary-container: '#00566a'
  inverse-primary: '#00677f'
  secondary: '#aec6ff'
  on-secondary: '#002e6a'
  secondary-container: '#4f8eff'
  on-secondary-container: '#00275e'
  tertiary: '#ffd4ca'
  on-tertiary: '#630f00'
  tertiary-container: '#ffad9a'
  on-tertiary-container: '#9b1d00'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#b6ebff'
  primary-fixed-dim: '#47d6ff'
  on-primary-fixed: '#001f28'
  on-primary-fixed-variant: '#004e60'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#aec6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004396'
  tertiary-fixed: '#ffdad2'
  tertiary-fixed-dim: '#ffb4a3'
  on-tertiary-fixed: '#3d0600'
  on-tertiary-fixed-variant: '#8c1900'
  background: '#0f141a'
  on-background: '#dfe2ec'
  surface-variant: '#31353d'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 54px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 26px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 26px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.04em
  label-md:
    fontFamily: Space Grotesk
    fontSize: 13px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.06em
  label-sm:
    fontFamily: Space Grotesk
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.08em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  gutter-desktop: 2rem
  margin: 1rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system is engineered specifically for dynamic in-vehicle environments, driver-oriented ergonomics, and live radio broadcasting. It merges high-contrast automotive heads-up interfaces with the playful, chunky tactility of community-driven navigation platforms.

### Personality & Vibe
- **Cockpit Ergonomics:** Instantaneous glanceability, hyper-legible focal points, and oversized touch targets calibrated for in-transit or dashboard use.
- **Electric Transmission:** Vibrant neon broadcast beacons layered over deep nocturnal chassis tones, evoking the kinetic thrill of late-night frequency scanning and live audio transmission.
- **Tactile Precision:** Chunky, pillowy containers paired with vivid status glows, giving the interface a mechanical yet modern tactile quality akin to physical cockpit instrument panels.

### Target Audience
Drivers, mobile commuters, and radio station enthusiasts who demand rapid physical inputs, zero visual ambiguity at arms-length, and instant situational awareness of live station status and locutor broadcast streams.

## Colors

The palette establishes an aggressive dark-mode automotive baseline to mitigate driver eye fatigue while maximizing signal clarity through glowing, high-contrast markers.

### Palette Architecture
- **Midnight Dashboard Base (`#11151C`):** Foundational canvas tone, representing the recessed cabin interior.
- **Elevated Console Slate (`#1B222E`):** Primary card, tray, and tactile surface layer.
- **Console Surface Highlight (`#263142`):** Active or pressed component base, border stroke, and divider tint.
- **Electric Signal Cyan (`#00D2FF`):** Core brand and interaction accent; used for dynamic controls, scrubbers, and primary touch highlights.
- **Deep Transmission Blue (`#0077FF`):** Supporting structure for active states, selected navigation bars, and primary gradients.
- **Live On-Air Coral (`#FF5A36`):** Dedicated broadcast alert color. Used exclusively for "LIVE", locutor mic-open badges, and critical road/station emergencies.
- **Locutor Active Lime (`#00E676`):** Audio flowing, tuner locked, and real-time voice indicator.

### Functional Ratios
Surfaces strictly occupy deep midnight slate (65%), structural layout and containers sit in intermediate console slate (25%), and electric luminous accents claim focused visual real estate (10%) to preserve night-vision comfort.

## Typography

Typography prioritizes extreme legible recognition under glance-conditions. 

- **Primary Typeface (`Plus Jakarta Sans`):** Selected for its wide geometric counters, contemporary curves, and solid weights that resist vibration blur. Headline and display levels lean heavily on 700 and 800 weights.
- **Instrument/Telemetry Typeface (`Space Grotesk`):** Applied exclusively to metadata, broadcast call-signs (e.g., `104.5 FM`, `LOCUTOR ON`), kHz/MHz tags, and timing chips to replicate rugged cockpit instrumentation.
- **Glance Scale Hierarchy:** Primary track titles and radio station frequencies are rendered at minimum `body-lg` or `headline-md` even in secondary cards. Never drop below 11px on system telemetry.

## Layout & Spacing

The layout model is constructed on an 8pt spatial baseline optimized for thumb-reach and dashboard cradles.

### Ergonomics & Grid
- **Single-Handed Thumb Reach:** Primary interactive drivers (Station Changer, PTT Talkback, Big Play/Pause) remain strictly locked in the lower 40% of the display viewport.
- **Touch Target Threshold:** Absolute minimum hit zone is 56px × 56px for in-drive interaction (48px strictly allowed only for auxiliary metadata chips).
- **Responsive Fluid Adaptation:**
  - **Phone Portrait (Handheld / Windshield Dock):** 4-column layout; full-width stacked cockpit cards; prominent persistent lower control bar.
  - **Phone Landscape / Tablet (CarPlay / Android Auto style mounts):** 8-to-12 column asymmetrical grid. Persistent left-aligned oversized player hub (40% width) coupled with scrolling frequency/station queue on the right (60% width).

## Elevation & Depth

Depth is tactile and mechanical rather than airy or paper-like. Layers feel molded into an automotive console panel.

### Surface Tiers
- **Tier 0 (Dashboard Base):** Background `#11151C`. Zero elevation, pure absorption.
- **Tier 1 (Recessed Console):** `#181F2A` with a 1px inner drop stroke (`rgba(0, 0, 0, 0.45)`) for inset dials, station history tracks, and channel switch tracks.
- **Tier 2 (Tactile Cockpit Cards):** `#1B222E` layered with a 1.5px top highlight border (`rgba(255, 255, 255, 0.08)`) and a bold directional drop shadow (`0px 8px 16px rgba(0, 0, 0, 0.40)`).
- **Tier 3 (Floating Driver Controls):** Floating circular actuators and hero buttons utilize ambient neon back-glows. The play button and voice broadcast switch emit a tinted aura: `0px 4px 24px rgba(0, 210, 255, 0.35)` when idle, ramping to `0px 6px 32px rgba(0, 230, 118, 0.50)` during active live voice lock.

### Structural Seams
No delicate hairline dividers. Use 2px to 4px structural gutters or tone transitions (`#263142`) to delineate sections.

## Shapes

Shapes utilize chunky, exaggerated pill profiles inspired by automotive toggles and playful GPS tokens.

- **Squircle & Capsule DNA:** Containers adopt pill shapes (`rounded-xl` and full capsule caps) to provide a soft, non-punitive physical presence that guides finger landing zones without requiring sharp visual tracking.
- **Concentric Soundwave Rings:** Indicators utilize nested concentric circles pulsing outwards from the center channel logo, conveying active broadcast signals.

## Components

### Buttons
- **Hero Driver Action (Play / Mic Trigger):** Full circle (72px to 88px) or massive pill container. Solid fill using `#00D2FF` or `#00E676` with heavy dark typography (`#11151C`). Active press reveals an indented physical snap behavior (scale: 0.94).
- **Station Quick-Jump:** Chunky rounded cards with distinct left-edge color-coded status stripes (4px).

### Chips & Telemetry Tags
- **"LIVE ON-AIR" Badge:** Capsule shape, background `#FF5A36`, label in `Space Grotesk Bold` (`#FFFFFF`), flanked by a pulsing white dot beacon.
- **"LOCUTOR ACTIVE" Pill:** Background `rgba(0, 230, 118, 0.15)` with a solid `#00E676` border stroke and matching neon green label.

### Sound Wave Ring States
- Centered on the master locutor dial. Concentric stroke rings (2px) radiating outward in three staggered animated rings using electric cyan at descending opacities (60%, 30%, 10%).

### Input Fields & Search
- Low-distraction voice-first inputs. Large search and tuning capsules (56px height) with `#1B222E` background, `#263142` border, and prominent high-contrast cyan microphone action pins.

### Cards & Station Modules
- Padded with `space-md` (16px), elevated surface background `#1B222E`. Metadata sits separated from title hierarchies, displaying frequency in high-impact telemetry styling.