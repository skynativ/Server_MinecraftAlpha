package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CommandSpreadPlayers extends CommandAbstract {

    public CommandSpreadPlayers() {}

    public String getCommand() {
        return "spreadplayers";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.spreadplayers.usage";
    }

    public void execute(ICommandListener icommandlistener, String[] astring) {
        if (astring.length < 6) {
            throw new ExceptionUsage("commands.spreadplayers.usage", new Object[0]);
        } else {
            byte b0 = 0;
            BlockPosition blockposition = icommandlistener.getChunkCoordinates();
            double d0 = (double) blockposition.getX();
            int i = b0 + 1;
            double d1 = b(d0, astring[b0], true);
            double d2 = b((double) blockposition.getZ(), astring[i++], true);
            double d3 = a(astring[i++], 0.0D);
            double d4 = a(astring[i++], d3 + 1.0D);
            boolean flag = d(astring[i++]);
            ArrayList arraylist = Lists.newArrayList();

            while (i < astring.length) {
                String s = astring[i++];

                if (PlayerSelector.isPattern(s)) {
                    List list = PlayerSelector.getPlayers(icommandlistener, s, Entity.class);

                    if (list.size() == 0) {
                        throw new ExceptionEntityNotFound();
                    }

                    arraylist.addAll(list);
                } else {
                    EntityPlayer entityplayer = MinecraftServer.getServer().getPlayerList().getPlayer(s);

                    if (entityplayer == null) {
                        throw new ExceptionPlayerNotFound();
                    }

                    arraylist.add(entityplayer);
                }
            }

            icommandlistener.a(EnumCommandResult.AFFECTED_ENTITIES, arraylist.size());
            if (arraylist.isEmpty()) {
                throw new ExceptionEntityNotFound();
            } else {
                icommandlistener.sendMessage(new ChatMessage("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), new Object[] { Integer.valueOf(arraylist.size()), Double.valueOf(d4), Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3)}));
                this.a(icommandlistener, arraylist, new Location2D(d1, d2), d3, d4, ((Entity) arraylist.get(0)).world, flag);
            }
        }
    }

    private void a(ICommandListener icommandlistener, List list, Location2D location2d, double d0, double d1, World world, boolean flag) {
        Random random = new Random();
        double d2 = location2d.a - d1;
        double d3 = location2d.b - d1;
        double d4 = location2d.a + d1;
        double d5 = location2d.b + d1;
        Location2D[] alocation2d = this.a(random, flag ? this.b(list) : list.size(), d2, d3, d4, d5);
        int i = this.a(location2d, d0, world, random, d2, d3, d4, d5, alocation2d, flag);
        double d6 = this.a(list, world, alocation2d, flag);

        a(icommandlistener, this, "commands.spreadplayers.success." + (flag ? "teams" : "players"), new Object[] { Integer.valueOf(alocation2d.length), Double.valueOf(location2d.a), Double.valueOf(location2d.b)});
        if (alocation2d.length > 1) {
            icommandlistener.sendMessage(new ChatMessage("commands.spreadplayers.info." + (flag ? "teams" : "players"), new Object[] { String.format("%.2f", new Object[] { Double.valueOf(d6)}), Integer.valueOf(i)}));
        }

    }

    private int b(List list) {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityHuman) {
                hashset.add(((EntityHuman) entity).getScoreboardTeam());
            } else {
                hashset.add((Object) null);
            }
        }

        return hashset.size();
    }

    private int a(Location2D location2d, double d0, World world, Random random, double d1, double d2, double d3, double d4, Location2D[] alocation2d, boolean flag) {
        boolean flag1 = true;
        double d5 = 3.4028234663852886E38D;

        int i;

        for (i = 0; i < 10000 && flag1; ++i) {
            flag1 = false;
            d5 = 3.4028234663852886E38D;

            int j;
            Location2D location2d1;

            for (int k = 0; k < alocation2d.length; ++k) {
                Location2D location2d2 = alocation2d[k];

                j = 0;
                location2d1 = new Location2D();

                for (int l = 0; l < alocation2d.length; ++l) {
                    if (k != l) {
                        Location2D location2d3 = alocation2d[l];
                        double d6 = location2d2.a(location2d3);

                        d5 = Math.min(d6, d5);
                        if (d6 < d0) {
                            ++j;
                            location2d1.a += location2d3.a - location2d2.a;
                            location2d1.b += location2d3.b - location2d2.b;
                        }
                    }
                }

                if (j > 0) {
                    location2d1.a /= (double) j;
                    location2d1.b /= (double) j;
                    double d7 = (double) location2d1.b();

                    if (d7 > 0.0D) {
                        location2d1.a();
                        location2d2.b(location2d1);
                    } else {
                        location2d2.a(random, d1, d2, d3, d4);
                    }

                    flag1 = true;
                }

                if (location2d2.a(d1, d2, d3, d4)) {
                    flag1 = true;
                }
            }

            if (!flag1) {
                Location2D[] alocation2d1 = alocation2d;
                int i1 = alocation2d.length;

                for (j = 0; j < i1; ++j) {
                    location2d1 = alocation2d1[j];
                    if (!location2d1.b(world)) {
                        location2d1.a(random, d1, d2, d3, d4);
                        flag1 = true;
                    }
                }
            }
        }

        if (i >= 10000) {
            throw new CommandException("commands.spreadplayers.failure." + (flag ? "teams" : "players"), new Object[] { Integer.valueOf(alocation2d.length), Double.valueOf(location2d.a), Double.valueOf(location2d.b), String.format("%.2f", new Object[] { Double.valueOf(d5)})});
        } else {
            return i;
        }
    }

    private double a(List list, World world, Location2D[] alocation2d, boolean flag) {
        double d0 = 0.0D;
        int i = 0;
        HashMap hashmap = Maps.newHashMap();

        for (int j = 0; j < list.size(); ++j) {
            Entity entity = (Entity) list.get(j);
            Location2D location2d;

            if (flag) {
                ScoreboardTeamBase scoreboardteambase = entity instanceof EntityHuman ? ((EntityHuman) entity).getScoreboardTeam() : null;

                if (!hashmap.containsKey(scoreboardteambase)) {
                    hashmap.put(scoreboardteambase, alocation2d[i++]);
                }

                location2d = (Location2D) hashmap.get(scoreboardteambase);
            } else {
                location2d = alocation2d[i++];
            }

            entity.enderTeleportTo((double) ((float) MathHelper.floor(location2d.a) + 0.5F), (double) location2d.a(world), (double) MathHelper.floor(location2d.b) + 0.5D);
            double d1 = Double.MAX_VALUE;

            for (int k = 0; k < alocation2d.length; ++k) {
                if (location2d != alocation2d[k]) {
                    double d2 = location2d.a(alocation2d[k]);

                    d1 = Math.min(d2, d1);
                }
            }

            d0 += d1;
        }

        d0 /= (double) list.size();
        return d0;
    }

    private Location2D[] a(Random random, int i, double d0, double d1, double d2, double d3) {
        Location2D[] alocation2d = new Location2D[i];

        for (int j = 0; j < alocation2d.length; ++j) {
            Location2D location2d = new Location2D();

            location2d.a(random, d0, d1, d2, d3);
            alocation2d[j] = location2d;
        }

        return alocation2d;
    }
}
