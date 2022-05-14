# MiniGameWorld-Controller
- The OPs can control almost things of MiniGameWord with players (Non-OP players are under control by the OPs)

# Rules
- Non-OP players can NOT use all commands and NOT access via sign block of MiniGameWorld
- MiniGame will never start without the OPs command (`/mwc start [<minigame>]`)
- When a player becomes an OP, rejoin the server to get all permissions

---

# Commands
- `/mwc start [<minigame>]`: start <minigame> minigame
- `/mwc finish [<minigame>]`: finish <minigame> minigame
- `/mwc <join | leave | view | unview> <minigame> [<player> <player> <player> ...]`: Control all non-op players or specific players (if player arguments are not given, targets are every non-OP players)

---

# Menu
![image](https://user-images.githubusercontent.com/61288262/166116384-f0ca54f4-0c5b-4861-95cf-e5602b370c42.png)
- Command: `/mw menu`
- Control only works when Control mode is enabled
- Icon works differently on player's position(in/out of minigame)

## Icons
### `Control`
- Click to switch mode (toggle)
- With `Control` state, menu icon works on control mode
- With `Default` state, menu icon works on default mode

### `Start`
- `in-game`: Start game the player is participating in
- `out-of-game`: Start all games

### `Leave`
- `in-game`: Leave or finish game the player is participating in with all participants and viewers
- `out-of-game`: Leave or finish all games

### `Minigame icons`
- `join`: Left-click (All players join the game)
- `view`: Right-click (All players view the game)
- `leave`: Q (Leave or finish game)
- `unview`: Shift + Q (All game viewers quit view)
- `start`: Shift + Left-click (clicked minigame will start)
















