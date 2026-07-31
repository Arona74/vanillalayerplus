# Vanilla+ Layers — Arona74 fork

A fork of [Fellteros/vanillalayerplus](https://github.com/Fellteros/vanillalayerplus), a Fabric mod
that adds layer blocks — stackable two-pixel-tall slices, like snow layers — for most vanilla blocks.

This fork exists to keep the mod running on more Minecraft versions than upstream currently targets,
and to fix a handful of bugs found along the way. All credit for the mod itself goes to
**Fellter**; everything here builds on their work.

## Supported versions

| Minecraft | Branch | Mod version | Java |
|---|---|---|---|
| 26.2 | [`26.2`](../../tree/26.2) | `4.0.0+26.2` | 25 |
| 1.21.11 | [`1.21.11`](../../tree/1.21.11) | `4.0.0+1.21.11` | 21 |
| 1.21.6 – 1.21.7 | [`1.21.7`](../../tree/1.21.7) | `4.0.0+1.21.6-7` | 21 |
| 1.21.1 | [`1.21.1`](../../tree/1.21.1) | `4.0.0+1.21.1` | 21 |
| 1.20.1 | [`1.20.1`](../../tree/1.20.1) | `4.0.0+1.20.1` | 17 |

Upstream targets 1.21.6–1.21.7. Every other version in that table is added by this fork.

## What this fork changes

### New blocks

- **Terracotta layer.** All sixteen coloured terracotta layers and all sixteen glazed variants
  already existed, but plain terracotta had been missed.
- **Red sand, tinted glass, muddy mangrove roots and reinforced deepslate layers.** Four vanilla
  full blocks that never had layer variants.
- **Cinnabar and sulfur layers** *(26.2 only)* — cinnabar, polished, bricks and chiseled, plus
  sulfur, potent, polished, bricks and chiseled. Stonecutting chains mirror vanilla exactly.

These came out of a sweep that compared the mod's registrations against every full-cube block in
each supported Minecraft version. All five branches are now complete for the version they target,
apart from functional and block-entity blocks (barrels, note blocks, TNT and similar), which are
intentionally excluded.

### Behaviour

- **Layers fall when their support is removed** instead of silently disappearing, matching how sand
  and gravel behave.

### Fixes

- **Layer blocks could be left floating in mid-air** when their support was removed. Dirt path,
  farmland and coral layers were affected, plus sand layers in one edge case.
- **Stonecutter recipes never unlocked** in the recipe book. Their unlock advancements pointed at
  the `minecraft` namespace instead of the mod's own, so they referenced recipes that do not exist.
  This affected every version.
- **On 1.20.1, recipes, loot tables and block tags never loaded at all** — they had been written
  under the directory names used by newer Minecraft versions. Crafting, block drops and tool tags
  were all broken.
- **On 26.2, translucent blocks rendered incorrectly.** Render layers are no longer registered at
  runtime in that version; they are derived from the block's texture instead.

### Under the hood

- **26.2 moves to Mojang's official mappings.** Yarn is discontinued from Minecraft 26.1 onward, so
  this was required rather than optional.
- 26.2 needs **Java 25** and Gradle 9; the 1.21.x branches use Java 21, and 1.20.1 uses Java 17.

## Building

Each Minecraft version lives on its own branch. Check one out and run:

```bash
./gradlew build          # jar lands in build/libs/
./gradlew runDatagen     # regenerate assets and data after changing blocks
```

Gradle must run on the JDK listed in the table above — for 26.2 this means Gradle's own JVM, not
just the compile target, or the build fails during configuration.

## A note on how this fork is developed

I use Claude AI as a development aid, mainly for the repetitive parts of multi-version maintenance:
porting a change across five branches, tracking down what a renamed Minecraft API became, and
running the coverage comparisons that found the missing blocks.

That is where the help stops. I decide what goes in, I read the changes, and I test in-game before
releasing. Several things in the list above exist precisely because testing caught them — the
floating-block bug was found by placing a farmland layer against a wall and breaking the wall, not
by reading code. Fixes get re-tested after the fact, not assumed.

I am saying this because "AI-assisted" often means code nobody checked. That is not what this is.
If you find a bug anyway, please open an issue — I would rather hear about it.

## Credits and licence

- Original mod and all its design work: **Fellter** —
  [GitHub](https://github.com/Fellteros/vanillalayerplus) ·
  [Modrinth](https://modrinth.com/mod/vanilla+-layers)
- This fork: **Arona74**

Licensed under **CC BY-NC 4.0**, the same licence as upstream. See [LICENSE](LICENSE).
