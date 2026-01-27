package pij.tile;

public enum Tile {
    A('a', 1),
    B('b', 3),
    C('c', 4),
    D('d', 2),
    E('e', 2),
    F('f', 4),
    G('g', 3),
    H('h', 4),
    I('i', 1),
    J('j', 11),
    K('k', 6),
    L('l', 1),
    M('m', 3),
    N('n', 1),
    O('o', 1),
    P('p', 3),
    Q('q', 12),
    R('r', 1),
    S('s', 1),
    T('t', 1),
    U('u', 1),
    V('v', 4),
    W('w', 4),
    X('x', 9),
    Y('y', 5),
    Z('z', 9),
    WILDCARD('_', 8);

    Tile(char letter, int tileMultiplier) {
        this.letter = letter;
        this.tileMultiplier = tileMultiplier;
    }

    private final char letter;
    private final int tileMultiplier;

    public char getLetter() {
        return this.letter;
    }

    public int getTileMultiplier() {
        return tileMultiplier;
    }
}
