package cn.edu.usst.mud;
// MUD.java - 修复版RPG游戏
import java.io.*;
import java.util.*;

// ==============================
// 1. 物品类
// ==============================
class Item implements Serializable {
    private String name;
    private String description;
    private int value;

    public Item(String name, String description) {
        this(name, description, 0);
    }

    public Item(String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return name + " - " + description;
    }
}

// ==============================
// 2. 技能类
// ==============================
class Skill implements Serializable {
    private String name;
    private int damage;
    private int mpCost;

    public Skill(String name, int damage, int mpCost) {
        this.name = name;
        this.damage = damage;
        this.mpCost = mpCost;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getMpCost() { return mpCost; }

    @Override
    public String toString() {
        return name + " (伤害:" + damage + ", 消耗MP:" + mpCost + ")";
    }
}

// ==============================
// 3. 角色基类
// ==============================
abstract class Character implements Serializable {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int mp;
    protected int maxMp;
    protected int attack;
    protected int defense;
    protected int level;

    public Character(String name, int maxHp, int maxMp, int attack, int defense, int level) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
        this.attack = attack;
        this.defense = defense;
        this.level = level;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getLevel() { return level; }

    public void setHp(int hp) {
        this.hp = Math.min(Math.max(0, hp), maxHp);
    }

    public void setMp(int mp) {
        this.mp = Math.min(Math.max(0, mp), maxMp);
    }

    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setLevel(int level) { this.level = level; }

    public boolean isAlive() { return hp > 0; }

    public void receiveDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense / 5);
        hp = Math.max(0, hp - actualDamage);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void restoreMp(int amount) {
        mp = Math.min(maxMp, mp + amount);
    }

    public void displayStatus() {
        System.out.println("=== " + name + " 状态 ===");
        System.out.println("等级: " + level);
        System.out.println("HP: " + hp + "/" + maxHp);
        System.out.println("MP: " + mp + "/" + maxMp);
        System.out.println("攻击: " + attack + "  防御: " + defense);
    }
}

// ==============================
// 4. 玩家类
// ==============================
class Player extends Character {
    private List<Skill> skills;
    private List<Item> inventory;
    private int experience;
    private int gold;
    private int maxExperience;

    public Player(String name) {
        super(name, 100, 50, 10, 5, 1);
        this.skills = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.experience = 0;
        this.gold = 50;
        this.maxExperience = 100;
        initializeSkills();
    }

    private void initializeSkills() {
        skills.add(new Skill("普通攻击", 10, 0));
        skills.add(new Skill("重击", 15, 5));
        skills.add(new Skill("治疗术", 0, 8));
    }

    public List<Skill> getSkills() { return skills; }
    public List<Item> getInventory() { return inventory; }
    public int getGold() { return gold; }
    public int getExperience() { return experience; }
    public int getMaxExperience() { return maxExperience; }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendGold(int amount) {
        gold -= amount;
    }

    public Skill useSkill(int index) {
        if (index >= 0 && index < skills.size()) {
            Skill skill = skills.get(index);
            if (mp >= skill.getMpCost()) {
                mp -= skill.getMpCost();
                return skill;
            }
        }
        return null;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public void showInventory() {
        System.out.println("=== 物品栏 ===");
        System.out.println("金币: " + gold);
        if (inventory.isEmpty()) {
            System.out.println("物品栏为空");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                System.out.println(i + ". " + inventory.get(i));
            }
        }
    }

    public void useItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            Item item = inventory.get(index);
            if (item.getName().contains("药水")) {
                heal(30);
                System.out.println("使用了 " + item.getName() + "，恢复30点HP");
                inventory.remove(index);
            }
        }
    }

    public void addGoldMessage(int amount) {
        gold += amount;
        System.out.println("获得了 " + amount + " 金币");
    }

    public void addExperience(int exp) {
        experience += exp;
        System.out.println("获得 " + exp + " 点经验值");
        if (experience >= maxExperience) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        experience -= maxExperience;
        maxExperience = (int)(maxExperience * 1.5);

        maxHp += 20;
        maxMp += 10;
        attack += 2;
        defense += 1;
        hp = maxHp;
        mp = maxMp;

        System.out.println("🎉 恭喜！等级提升到 " + level + " 级！");
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("经验: " + experience + "/" + maxExperience);
        System.out.println("金币: " + gold);
        System.out.println("技能数量: " + skills.size());
        System.out.println("物品数量: " + inventory.size());
    }
}

// ==============================
// 5. 敌人类
// ==============================
class Enemy extends Character {
    private int experienceReward;
    private int goldReward;
    private Item dropItem;

    public Enemy(String name, int maxHp, int maxMp, int attack, int defense) {
        // 根据生命值估算等级
        super(name, maxHp, maxMp, attack, defense, calculateLevel(maxHp));
        this.experienceReward = maxHp / 2;
        this.goldReward = maxHp / 4;

        Random rand = new Random();
        if (rand.nextDouble() < 0.3) {
            String[] items = {"治疗药水", "魔法药水", "小型生命药剂"};
            dropItem = new Item(items[rand.nextInt(items.length)], "击败敌人获得的战利品");
        }
    }

    // 根据生命值计算等级
    private static int calculateLevel(int maxHp) {
        if (maxHp <= 50) return 1;
        if (maxHp <= 80) return 2;
        if (maxHp <= 120) return 3;
        if (maxHp <= 180) return 4;
        return 5;
    }

    public int getExperienceReward() { return experienceReward; }
    public int getGoldReward() { return goldReward; }
    public Item getDropItem() { return dropItem; }

    public int attack() {
        return attack + new Random().nextInt(5);
    }

    public String getNextIntention() {
        String[] intentions = {"攻击", "防御", "强化"};
        return intentions[new Random().nextInt(intentions.length)];
    }
}

// ==============================
// 6. NPC类
// ==============================
class NPC extends Character {
    private boolean friendly;
    private String dialogue;
    private List<Item> shopItems;

    public NPC(String name, int maxHp, int maxMp, int attack, int defense,
               boolean friendly, String dialogue) {
        super(name, maxHp, maxMp, attack, defense, 5);
        this.friendly = friendly;
        this.dialogue = dialogue;
        this.shopItems = new ArrayList<>();
    }

    public boolean isFriendly() { return friendly; }
    public String getDialogue() { return dialogue; }

    public void setShopItems(List<Item> items) {
        shopItems.addAll(items);
    }

    public void interact(Player player) {
        System.out.println("\n" + name + ": \"" + dialogue + "\"");

        if (friendly) {
            if (!shopItems.isEmpty()) {
                System.out.println("\n" + name + " 的商店:");
                System.out.println("你拥有金币: " + player.getGold());
                for (int i = 0; i < shopItems.size(); i++) {
                    Item item = shopItems.get(i);
                    System.out.println(i + ". " + item.getName() + " - 价格:" + item.getValue());
                }
                System.out.print("输入要购买的物品编号 (或-1离开): ");
                Scanner scanner = new Scanner(System.in);
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    if (choice >= 0 && choice < shopItems.size()) {
                        Item item = shopItems.get(choice);
                        if (player.getGold() >= item.getValue()) {
                            player.spendGold(item.getValue());
                            player.addItem(item);
                            System.out.println("购买了 " + item.getName());
                        } else {
                            System.out.println("金币不足！");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("离开商店");
                }
            }
        } else {
            System.out.println("这个NPC似乎不怀好意...");
            System.out.print("是否发起攻击？(y/n): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("y")) {
                Enemy enemy = new Enemy(name, maxHp, maxMp, attack, defense);
                BattleSystem battle = new BattleSystem(player, enemy);
                battle.startBattle();
            }
        }
    }
}

// ==============================
// 7. 房间类
// ==============================
class Room implements Serializable {
    private String name;
    private String description;
    private List<Item> items;
    private List<NPC> npcs;
    private List<Enemy> enemies;
    private Map<String, Room> exits;
    private boolean visited;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.exits = new HashMap<>();
        this.visited = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Item> getItems() { return items; }
    public List<NPC> getNPCs() { return npcs; }
    public List<Enemy> getEnemies() { return enemies; }
    public Map<String, Room> getExits() { return exits; }
    public boolean isVisited() { return visited; }

    public void setVisited(boolean visited) { this.visited = visited; }

    public void addExit(String direction, Room room) {
        exits.put(direction, room);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public Item takeItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public void displayInfo(Player player) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("📍 " + name);
        System.out.println("-".repeat(40));
        System.out.println(description);

        if (!items.isEmpty()) {
            System.out.println("\n📦 物品:");
            for (Item item : items) {
                System.out.println("  • " + item.getName());
            }
        }

        if (!npcs.isEmpty()) {
            System.out.println("\n👤 NPC:");
            for (NPC npc : npcs) {
                System.out.println("  • " + npc.getName());
            }
        }

        if (!enemies.isEmpty()) {
            System.out.println("\n👹 敌人:");
            for (Enemy enemy : enemies) {
                System.out.println("  • " + enemy.getName() + " Lv." + enemy.getLevel());
            }
        }

        if (!exits.isEmpty()) {
            System.out.println("\n🚪 出口:");
            for (String dir : exits.keySet()) {
                System.out.println("  • " + dir + " → " + exits.get(dir).getName());
            }
        }

        visited = true;
    }
}

// ==============================
// 8. 战斗系统
// ==============================
class BattleSystem {
    private Player player;
    private Enemy enemy;
    private Scanner scanner;
    private boolean playerDefending;
    private boolean enemyDefending;

    public BattleSystem(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.scanner = new Scanner(System.in);
        this.playerDefending = false;
        this.enemyDefending = false;
    }

    public boolean startBattle() {
        System.out.println("\n⚔️ 战斗开始！ vs " + enemy.getName() + " Lv." + enemy.getLevel());

        while (player.isAlive() && enemy.isAlive()) {
            // 玩家回合
            playerTurn();
            if (!enemy.isAlive()) break;

            // 敌人回合
            enemyTurn();

            // 重置防御状态
            playerDefending = false;
            enemyDefending = false;
        }

        return endBattle();
    }

    private void playerTurn() {
        System.out.println("\n=== 你的回合 ===");
        System.out.println("你的HP: " + player.getHp() + "/" + player.getMaxHp());
        System.out.println("敌人HP: " + enemy.getHp() + "/" + enemy.getMaxHp());

        // 显示敌人意图
        System.out.println("敌人意图: " + enemy.getNextIntention());

        System.out.println("\n可用的技能:");
        List<Skill> skills = player.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            System.out.println(i + ". " + skills.get(i));
        }

        System.out.print("选择技能编号 (或-1防御): ");
        try {
            String input = scanner.nextLine();
            if (input.equals("-1")) {
                playerDefending = true;
                System.out.println("你选择了防御");
                return;
            }

            int choice = Integer.parseInt(input);
            Skill skill = player.useSkill(choice);

            if (skill != null) {
                if (skill.getName().equals("治疗术")) {
                    player.heal(20);
                    System.out.println("使用了治疗术，恢复20点HP");
                } else {
                    int damage = skill.getDamage() + player.getAttack() / 2;

                    // 敌人防御时伤害减半
                    if (enemyDefending) {
                        damage = (int)(damage * 0.5);
                        System.out.println("敌人处于防御状态，伤害减半！");
                    }

                    enemy.receiveDamage(damage);
                    System.out.println("使用了 " + skill.getName() + "，造成 " + damage + " 点伤害");
                }
            } else {
                System.out.println("MP不足或无效的选择！");
            }
        } catch (Exception e) {
            System.out.println("无效的输入");
        }
    }

    private void enemyTurn() {
        System.out.println("\n=== " + enemy.getName() + "的回合 ===");

        Random rand = new Random();
        int action = rand.nextInt(3);

        switch (action) {
            case 0: // 攻击
                int damage = enemy.attack();
                if (playerDefending) {
                    damage = (int)(damage * 0.5);
                    System.out.println("你处于防御状态，伤害减半！");
                }
                player.receiveDamage(damage);
                System.out.println(enemy.getName() + " 攻击了你，造成 " + damage + " 点伤害");
                break;
            case 1: // 防御
                enemyDefending = true;
                System.out.println(enemy.getName() + " 进入了防御状态");
                break;
            case 2: // 强化
                enemy.setAttack(enemy.getAttack() + 2);
                System.out.println(enemy.getName() + " 强化了自己，攻击力提升！");
                break;
        }
    }

    private boolean endBattle() {
        if (player.isAlive()) {
            victory();
            return true;
        } else {
            defeat();
            return false;
        }
    }

    private void victory() {
        System.out.println("\n🎉 战斗胜利！击败了 " + enemy.getName());

        // 奖励
        int exp = enemy.getExperienceReward();
        int gold = enemy.getGoldReward();
        player.addExperience(exp);
        player.addGoldMessage(gold);

        // 掉落物品
        Item drop = enemy.getDropItem();
        if (drop != null) {
            player.addItem(drop);
            System.out.println("获得了战利品: " + drop.getName());
        }

        // 恢复
        player.heal(player.getMaxHp() / 4);
        player.restoreMp(player.getMaxMp() / 4);
        System.out.println("战斗后恢复了一些HP和MP");
    }

    private void defeat() {
        System.out.println("\n💀 战斗失败...");
        player.setHp(player.getMaxHp() / 2);
        System.out.println("你被复活了，但HP只剩一半");
    }
}

// ==============================
// 9. 游戏管理器
// ==============================
class GameManager implements Serializable {
    private Player player;
    private Room currentRoom;
    private Scanner scanner;
    private static final String SAVE_FILE = "mud_save.dat";

    public GameManager() {
        scanner = new Scanner(System.in);
        initializeGameWorld();
    }

    private void initializeGameWorld() {
        // 创建房间
        Room startRoom = new Room("起始大厅", "一个古老的大厅，中央有一个石制喷泉。");
        Room forest = new Room("幽暗森林", "茂密的森林，阳光难以穿透树冠。");
        Room cave = new Room("神秘洞穴", "潮湿的洞穴，墙壁上闪烁着微光。");
        Room village = new Room("宁静村庄", "一个安静的小村庄，村民们正在忙碌。");

        // 添加物品
        startRoom.addItem(new Item("治疗药水", "恢复生命值的红色药水", 10));
        startRoom.addItem(new Item("铁剑", "一把普通的铁剑", 30));

        forest.addItem(new Item("草药", "常见的治疗草药", 5));

        village.addItem(new Item("面包", "新鲜出炉的面包", 2));

        // 添加NPC
        NPC oldMan = new NPC("神秘老人", 100, 50, 15, 10, true,
                "勇敢的冒险者，小心森林里的怪物！");

        NPC blacksmith = new NPC("铁匠", 150, 30, 25, 20, true,
                "需要装备吗？我这里有好东西！");
        List<Item> shopItems = new ArrayList<>();
        shopItems.add(new Item("钢剑", "更锋利的剑", 50));
        shopItems.add(new Item("锁子甲", "提供良好防护", 80));
        shopItems.add(new Item("强效治疗药水", "恢复更多HP", 20));
        blacksmith.setShopItems(shopItems);

        startRoom.addNPC(oldMan);
        village.addNPC(blacksmith);

        // 添加敌人
        forest.addEnemy(new Enemy("森林狼", 60, 10, 12, 5));
        cave.addEnemy(new Enemy("洞穴蝙蝠", 40, 5, 8, 3));
        cave.addEnemy(new Enemy("岩石怪", 100, 20, 18, 15));

        // 连接房间
        startRoom.addExit("北", forest);
        startRoom.addExit("东", village);

        forest.addExit("南", startRoom);
        forest.addExit("东", cave);

        cave.addExit("西", forest);

        village.addExit("西", startRoom);

        currentRoom = startRoom;
    }

    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {

            GameSaveData data = new GameSaveData(player, currentRoom.getName());
            oos.writeObject(data);
            System.out.println("✅ 游戏已保存");

        } catch (IOException e) {
            System.out.println("❌ 保存失败: " + e.getMessage());
        }
    }

    public boolean loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {

            GameSaveData data = (GameSaveData) ois.readObject();
            this.player = data.getPlayer();
            System.out.println("✅ 游戏加载成功");
            return true;

        } catch (Exception e) {
            System.out.println("❌ 加载失败: " + e.getMessage());
            return false;
        }
    }

    public void play() {
        System.out.println("\n🎮 欢迎来到 MUD 游戏！");
        System.out.println("=".repeat(40));

        // 检查存档
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            System.out.print("检测到存档，是否加载？(y/n): ");
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("y")) {
                if (loadGame()) {
                    System.out.println("欢迎回来，" + player.getName() + "！");
                } else {
                    createNewCharacter();
                }
            } else {
                createNewCharacter();
            }
        } else {
            createNewCharacter();
        }

        boolean playing = true;
        while (playing && player.isAlive()) {
            displayMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    exploreCurrentRoom();
                    break;
                case "2":
                    moveToRoom();
                    break;
                case "3":
                    player.displayStatus();
                    break;
                case "4":
                    player.showInventory();
                    break;
                case "5":
                    interactWithEnvironment();
                    break;
                case "6":
                    saveGame();
                    break;
                case "7":
                    System.out.println("感谢游玩！");
                    playing = false;
                    break;
                case "help":
                    displayHelp();
                    break;
                default:
                    System.out.println("无效命令，输入 'help' 查看帮助");
            }
        }

        if (!player.isAlive()) {
            System.out.println("\n💀 你已死亡！游戏结束。");
            System.out.println("最终等级: " + player.getLevel());
        }

        scanner.close();
    }

    private void createNewCharacter() {
        System.out.println("\n=== 创建角色 ===");
        System.out.print("输入角色姓名: ");
        String name = scanner.nextLine();
        player = new Player(name);
        System.out.println("✨ 角色创建成功！");
        System.out.println("欢迎来到这个世界，" + name + "！");
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("📍 当前位置: " + currentRoom.getName());
        System.out.println("=".repeat(40));
        System.out.println("1. 探索当前房间");
        System.out.println("2. 移动到其他房间");
        System.out.println("3. 查看角色状态");
        System.out.println("4. 查看物品栏");
        System.out.println("5. 与环境互动");
        System.out.println("6. 保存游戏");
        System.out.println("7. 退出游戏");
        System.out.println("输入 'help' 查看帮助");
        System.out.print("选择操作: ");
    }

    private void exploreCurrentRoom() {
        currentRoom.displayInfo(player);

        // 如果有敌人，询问是否战斗
        List<Enemy> enemies = currentRoom.getEnemies();
        if (!enemies.isEmpty()) {
            Enemy enemy = enemies.get(0);
            System.out.print("\n发现敌人！是否发起攻击？(y/n): ");
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("y")) {
                BattleSystem battle = new BattleSystem(player, enemy);
                boolean victory = battle.startBattle();

                if (victory) {
                    currentRoom.removeEnemy(enemy);
                }
            }
        }
    }

    private void moveToRoom() {
        Map<String, Room> exits = currentRoom.getExits();
        if (exits.isEmpty()) {
            System.out.println("这个房间没有出口！");
            return;
        }

        System.out.println("\n出口:");
        for (String dir : exits.keySet()) {
            System.out.println("- " + dir + ": " + exits.get(dir).getName());
        }

        System.out.print("输入移动方向: ");
        String direction = scanner.nextLine();

        Room nextRoom = exits.get(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("移动到了: " + currentRoom.getName());
        } else {
            System.out.println("这个方向没有路！");
        }
    }

    private void interactWithEnvironment() {
        List<NPC> npcs = currentRoom.getNPCs();
        List<Item> items = currentRoom.getItems();

        if (npcs.isEmpty() && items.isEmpty()) {
            System.out.println("这个房间没有可互动的对象");
            return;
        }

        if (!npcs.isEmpty()) {
            System.out.println("\nNPC:");
            for (int i = 0; i < npcs.size(); i++) {
                System.out.println(i + ". 与 " + npcs.get(i).getName() + " 交谈");
            }
        }

        if (!items.isEmpty()) {
            System.out.println("\n物品:");
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + npcs.size()) + ". 拾取 " + items.get(i).getName());
            }
        }

        System.out.print("选择互动对象编号 (或输入-1取消): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == -1) return;

            if (choice < npcs.size()) {
                // 与NPC交谈
                npcs.get(choice).interact(player);
            } else if (choice < npcs.size() + items.size()) {
                // 拾取物品
                int itemIndex = choice - npcs.size();
                Item item = items.get(itemIndex);
                player.addItem(item);
                items.remove(itemIndex);
                System.out.println("拾取了: " + item.getName());
            } else {
                System.out.println("无效的选择");
            }
        } catch (Exception e) {
            System.out.println("请输入有效的数字");
        }
    }

    private void displayHelp() {
        System.out.println("\n=== 游戏帮助 ===");
        System.out.println("基本命令:");
        System.out.println("  1 - 探索当前房间");
        System.out.println("  2 - 移动到其他房间");
        System.out.println("  3 - 查看角色状态");
        System.out.println("  4 - 查看物品栏");
        System.out.println("  5 - 与环境互动");
        System.out.println("  6 - 保存游戏");
        System.out.println("  7 - 退出游戏");
        System.out.println("\n战斗说明:");
        System.out.println("  • 普通攻击：不消耗MP的基础攻击");
        System.out.println("  • 重击：消耗5MP，造成更高伤害");
        System.out.println("  • 治疗术：消耗8MP，恢复自身HP");
        System.out.println("  • 防御：减少受到的伤害");
        System.out.println("\n提示:");
        System.out.println("  • 击败敌人获得经验和金币");
        System.out.println("  • 升级可以提升属性");
        System.out.println("  • 商店可以购买装备和药水");
        System.out.println("  • 定期保存游戏以防进度丢失");
    }
}

// ==============================
// 10. 游戏数据保存类
// ==============================
class GameSaveData implements Serializable {
    private Player player;
    private String currentRoomName;

    public GameSaveData(Player player, String currentRoomName) {
        this.player = player;
        this.currentRoomName = currentRoomName;
    }

    public Player getPlayer() { return player; }
    public String getCurrentRoomName() { return currentRoomName; }
}

// ==============================
// 11. 主类
// ==============================
public class MUD {
    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.play();
    }
}