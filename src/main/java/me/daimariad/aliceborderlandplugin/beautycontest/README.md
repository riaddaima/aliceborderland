## Overview

This game is inspired from the King of Diamonds from Alice in Borderland, which is a 2020 Japanese science fiction thriller drama streaming television series based on the manga by Haro Aso.

The intended outcome of this repository is to experiment with CraftBukkit' Minecraft server API, [Spiggot](https://www.spigotmc.org/).

## [Game Overview](https://aliceinborderland.fandom.com/wiki/King_of_Diamonds)

![image](https://user-images.githubusercontent.com/75309623/210622365-47d606b4-7f0e-4e9d-9459-b85a80005ccc.png)


### Set-up
Player limit: 4

Time limit: 1 minute/round

5 minutes/first round & rounds with new rules

Upon entering, all players shackle themselves to a seat and confirm the operation of the tablet in front of them. Above their heads is a weight/equilibrium stand that fills Aqua Regia.

### Rules
**All players remaining**:

- All players select a number between 0 and 100 in the given time.
- The average of the values will be multiplied by 4/5ths (0.8).
- The person who chooses the number closest to the calculated number wins. This constitutes one round.
- All losers will lose a point.
- A new rule is introduced for every player eliminated. On the first round and all following rounds where a new rule is introduced, players are allotted 5 minutes as a way to get used to the rules.

**Game Clear**: Be the last person remaining.

**Game Over**: Reach -10 points.

Every player who gets a “Game Over” will cause the weight stand to tip over, dumping Aqua Regia and burning the victim

**4 players remaining:**

- If there are 2 people or more choose the same number, the number they choose becomes invalid, meaning they will lose a point even if the number is closest to 4/5ths the average.
  - Due to two players receiving a Game Over, this rule was introduced in the 3 players remaining announcement instead.
  
**3 players remaining:**

- If there is a person who chooses the exact correct number, the loser penalty is doubled.

**2 players remaining:**

- If someone chooses 0, the player who chooses 100 is the winner.
