# Modular Professions

Allows players to gain xp in different professions. Unlock the use of additional items as you level up. Configured by data packs. 


## Data Packs

Professions are defined by json files in the `modularprofessions` folder of your data packs. 

These files should have the following keys:

- `leveling`
- `triggers`
- `unlocked`

### Leveling

This data defines how quickly you level up in this profession as you gain experience (in the profession, not vanilla xp). 

### Triggers 


by default the break block trigger doesn't give xp if you have silk touch so it cant be exploited by breaking the same ore over and over but just in case you can set the field `allowSilkTouch=true` if you want

for the damage entity trigger, if you set the field `multiplyXpByDamage=true`, the amount of xp given will be multiplied by the damage delt so you dont incentivise using weaker weapons to get more hits and get more xp

for both the damage and kill entity, leaving the `weapon` or `entity` fields blank will match anything


### Unlocked


### ItemCollection

