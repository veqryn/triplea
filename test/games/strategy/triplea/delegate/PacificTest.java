package games.strategy.triplea.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import games.strategy.engine.data.ITestDelegateBridge;
import games.strategy.engine.data.PlayerID;
import games.strategy.engine.data.Route;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.TerritoryEffect;
import games.strategy.engine.data.Unit;
import games.strategy.engine.data.UnitType;
import games.strategy.engine.random.ScriptedRandomSource;
import games.strategy.triplea.xml.LoadGameUtil;
import games.strategy.util.IntegerMap;

// public class PacificTest extends TestCase
public class PacificTest extends DelegateTest {
  /** Creates new PacificTest */
  public PacificTest(final String name) {
    super(name);
  }

  // GameData m_data;
  // Define units
  // UnitType infantry;
  UnitType armor;
  UnitType artillery;
  UnitType marine;
  // UnitType fighter;
  // UnitType bomber;
  UnitType sub;
  UnitType destroyer;
  // UnitType carrier;
  UnitType battleship;
  // UnitType transport;
  // Define players
  PlayerID americans;
  PlayerID chinese;
  // PlayerID british;
  // PlayerID japanese;
  // Define territories
  Territory queensland;
  // Territory japan;
  Territory US;
  Territory NewBrit;
  Territory Midway;
  Territory Mariana;
  Territory Bonin;
  // Define Sea Zones
  Territory SZ4;
  Territory SZ5;
  Territory SZ7;
  Territory SZ8;
  Territory SZ10;
  Territory SZ16;
  Territory SZ20;
  Territory SZ24;
  Territory SZ25;
  Territory SZ27;
  ITestDelegateBridge bridge;
  MoveDelegate m_delegate;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    m_data = LoadGameUtil.loadGame("pacific test", "pacific_incomplete_test.xml");
    // Define units
    infantry = m_data.getUnitTypeList().getUnitType("infantry");
    armor = m_data.getUnitTypeList().getUnitType("armour");
    artillery = m_data.getUnitTypeList().getUnitType("artillery");
    marine = m_data.getUnitTypeList().getUnitType("marine");
    fighter = m_data.getUnitTypeList().getUnitType("fighter");
    bomber = m_data.getUnitTypeList().getUnitType("bomber");
    sub = m_data.getUnitTypeList().getUnitType("submarine");
    destroyer = m_data.getUnitTypeList().getUnitType("destroyer");
    carrier = m_data.getUnitTypeList().getUnitType("carrier");
    battleship = m_data.getUnitTypeList().getUnitType("battleship");
    transport = m_data.getUnitTypeList().getUnitType("transport");
    // Define players
    americans = m_data.getPlayerList().getPlayerID("Americans");
    chinese = m_data.getPlayerList().getPlayerID("Chinese");
    british = m_data.getPlayerList().getPlayerID("British");
    japanese = m_data.getPlayerList().getPlayerID("Japanese");
    // Define territories
    queensland = m_data.getMap().getTerritory("Queensland");
    japan = m_data.getMap().getTerritory("Japan");
    US = m_data.getMap().getTerritory("United States");
    NewBrit = m_data.getMap().getTerritory("New Britain");
    Midway = m_data.getMap().getTerritory("Midway");
    Mariana = m_data.getMap().getTerritory("Mariana");
    Bonin = m_data.getMap().getTerritory("Bonin");
    // Define Sea Zones
    SZ4 = m_data.getMap().getTerritory("4 Sea Zone");
    SZ5 = m_data.getMap().getTerritory("5 Sea Zone");
    SZ7 = m_data.getMap().getTerritory("7 Sea Zone");
    SZ8 = m_data.getMap().getTerritory("8 Sea Zone");
    SZ10 = m_data.getMap().getTerritory("10 Sea Zone");
    SZ16 = m_data.getMap().getTerritory("16 Sea Zone");
    SZ20 = m_data.getMap().getTerritory("20 Sea Zone");
    SZ24 = m_data.getMap().getTerritory("24 Sea Zone");
    SZ25 = m_data.getMap().getTerritory("25 Sea Zone");
    SZ27 = m_data.getMap().getTerritory("27 Sea Zone");
    bridge = getDelegateBridge(americans);
    bridge.setStepName("japaneseCombatMove");
    m_delegate = new MoveDelegate();
    m_delegate.initialize("MoveDelegate", "MoveDelegate");
    m_delegate.setDelegateBridgeAndPlayer(bridge);
    m_delegate.start();
  }

  private Collection<Unit> getUnits(final IntegerMap<UnitType> units, final Territory from) {
    final Iterator<UnitType> iter = units.keySet().iterator();
    final Collection<Unit> rVal = new ArrayList<Unit>(units.totalValues());
    while (iter.hasNext()) {
      final UnitType type = iter.next();
      rVal.addAll(from.getUnits().getUnits(type, units.getInt(type)));
    }
    return rVal;
  }

  @Override
  protected void tearDown() throws Exception {
    m_data = null;
  }

  @Override
  protected ITestDelegateBridge getDelegateBridge(final PlayerID player) {
    return GameDataTestUtil.getDelegateBridge(player, m_data);
  }

  public void testNonJapanAttack() {
    bridge.setStepName("japaneseEndTurn"); // this will get us to round 2
    bridge.setStepName("japaneseBattle");
    // Defending US infantry hit on a 2 (0 base)
    final List<Unit> infantryUS = infantry.create(1, americans);
    final Collection<TerritoryEffect> territoryEffects = TerritoryEffectHelper.getEffects(queensland);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    DiceRoll roll =
        DiceRoll.rollDice(infantryUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
    // Defending US marines hit on a 2 (0 base)
    final List<Unit> marineUS = marine.create(1, americans);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    roll = DiceRoll.rollDice(marineUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
    // Chinese units
    // Defending Chinese infantry hit on a 2 (0 base)
    final List<Unit> infantryChina = infantry.create(1, chinese);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    roll =
        DiceRoll.rollDice(infantryChina, true, chinese, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
  }

  public void testJapanAttackFirstRound() {
    bridge.setStepName("japaneseBattle");
    while (!m_data.getSequence().getStep().getName().equals("japaneseBattle")) {
      m_data.getSequence().next();
    }
    // >>> After patch normal to-hits will miss <<<
    // Defending US infantry miss on a 2 (0 base)
    final List<Unit> infantryUS = infantry.create(1, americans);
    final Collection<TerritoryEffect> territoryEffects = TerritoryEffectHelper.getEffects(queensland);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    DiceRoll roll =
        DiceRoll.rollDice(infantryUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(0, roll.getHits());
    // Defending US marines miss on a 2 (0 base)
    final List<Unit> marineUS = marine.create(1, americans);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    roll = DiceRoll.rollDice(marineUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(0, roll.getHits());
    // Chinese units
    // Defending Chinese infantry still hit on a 2 (0 base)
    final List<Unit> infantryChina = infantry.create(1, chinese);
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    roll =
        DiceRoll.rollDice(infantryChina, true, chinese, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
    // Defending US infantry hit on a 1 (0 base)
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {0}));
    roll =
        DiceRoll.rollDice(infantryUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
    // Defending US marines hit on a 1 (0 base)
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {0}));
    roll = DiceRoll.rollDice(marineUS, true, americans, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
    // Chinese units
    // Defending Chinese infantry still hit on a 2 (0 base)
    bridge.setRandomSource(new ScriptedRandomSource(new int[] {1}));
    roll =
        DiceRoll.rollDice(infantryChina, true, chinese, bridge, new MockBattle(queensland), "", territoryEffects, null);
    assertEquals(1, roll.getHits());
  }

  public void testCanLand2Airfields() {
    bridge.setStepName("americanCombatMove");
    final Route route = new Route();
    route.setStart(US);
    route.add(SZ5);
    route.add(SZ4);
    route.add(SZ10);
    route.add(SZ16);
    route.add(SZ27);
    route.add(NewBrit);
    final IntegerMap<UnitType> map = new IntegerMap<UnitType>();
    map.put(fighter, 1);
    final String results = m_delegate.move(getUnits(map, route.getStart()), route);
    assertValid(results);
  }

  public void testCanLand1AirfieldStart() {
    bridge.setStepName("americanCombatMove");
    final Route route = new Route();
    route.setStart(US);
    route.add(SZ5);
    route.add(SZ7);
    route.add(SZ8);
    route.add(SZ20);
    route.add(Midway);
    final IntegerMap<UnitType> map = new IntegerMap<UnitType>();
    map.put(fighter, 1);
    final String results = m_delegate.move(getUnits(map, route.getStart()), route);
    assertValid(results);
    // assertError( results);
  }

  public void testCanLand1AirfieldEnd() {
    bridge.setStepName("americanCombatMove");
    final Route route = new Route();
    route.setStart(US);
    route.add(SZ5);
    route.add(SZ7);
    route.add(SZ8);
    route.add(SZ20);
    route.add(Midway);
    final IntegerMap<UnitType> map = new IntegerMap<UnitType>();
    map.put(fighter, 1);
    final String results = m_delegate.move(getUnits(map, route.getStart()), route);
    assertValid(results);
  }

  public void testCanMoveNavalBase() {
    bridge.setStepName("americanNonCombatMove");
    final Route route = new Route();
    route.setStart(SZ5);
    route.add(SZ7);
    route.add(SZ8);
    route.add(SZ20);
    final IntegerMap<UnitType> map = new IntegerMap<UnitType>();
    map.put(fighter, 1);
    final String results = m_delegate.move(getUnits(map, route.getStart()), route);
    assertValid(results);
  }

  public void testJapaneseDestroyerTransport() {
    bridge = getDelegateBridge(japanese);
    m_delegate = new MoveDelegate();
    m_delegate.initialize("MoveDelegate", "MoveDelegate");
    m_delegate.setDelegateBridgeAndPlayer(bridge);
    bridge.setStepName("japaneseNonCombatMove");
    m_delegate.start();
    final IntegerMap<UnitType> map = new IntegerMap<UnitType>();
    map.put(infantry, 1);
    final Route route = new Route();
    route.setStart(Bonin);
    // movement to force boarding
    route.add(SZ24);
    // verify unit counts before move
    assertEquals(2, Bonin.getUnits().size());
    assertEquals(1, SZ24.getUnits().size());
    // validate movement
    final String results =
        m_delegate.move(getUnits(map, route.getStart()), route, route.getEnd().getUnits().getUnits());
    assertValid(results);
    // verify unit counts after move
    assertEquals(1, Bonin.getUnits().size());
    assertEquals(2, SZ24.getUnits().size());
  }
  // assertError( results);
}
