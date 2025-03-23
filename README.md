# GangsPlusAddon64
A spigot plugin that expands GangsPlus and adds user-friendly, but powerful GUIs.

Created by tbm00 for play.mc64.wtf.


## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS
- **MySQL**: REQUIRED
- **GangsPlus**: REQUIRED
- **PVPStats**: REQUIRED
- **Rep64**: REQUIRED
- **Logger64**: REQUIRED


## Commands
#### Player Commands 
- `/gangs` Open gang GUI
- `/gangs <gang/player>` Open GUI by player/gang

#### Admin Commands
- `/gangsadmin` Open gang admin GUI


## Permissions
#### Player Permissions
- `gangsplusaddon64.player` Ability to use player commands *(default: everyone)*

#### Admin Permissions
- `gangsplusaddon64.admin` Ability to use admin commands *(default: op)*


## Config
```
# GangsPlusAddon64 v0.0.4-beta by @tbm00
# https://github.com/tbm00/GangsPlusAddon64

enabled: true

mysql:
  host: 'host'
  port: 3306
  database: 'db'
  username: 'user'
  password: 'pass'
  useSSL: false
  hikari:
    maximumPoolSize: 16
    minimumPoolSize: 2
    idleTimeout: 240 # 4 minutes
    connectionTimeout: 30 # 30 seconds
    maxLifetime: 1800 # 30 minutes
    leakDetection:
      enabled: false
      threshold: 2 # 2 seconds

lang:
  prefix: "&8[&fGangs&8] &7"

feature:
  enabled: true
```