package com.lig.libby.bee.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "0000", id = "0000-lang-test-data", author = "iborisenko")
    public void insertLang(DB db) {
        DBCollection myCollection = db.getCollection(Lang.TABLE);
        List<BasicDBObject> lst = new ArrayList<>();
        lst.add(new BasicDBObject().append("_id", "en-US").append(Lang.NAME_COLUMN, "en-US"));
        lst.add(new BasicDBObject().append("_id", "en-CA").append(Lang.NAME_COLUMN, "en-CA"));
        lst.add(new BasicDBObject().append("_id", "spa").append(Lang.NAME_COLUMN, "spa"));
        lst.add(new BasicDBObject().append("_id", "ind").append(Lang.NAME_COLUMN, "ind"));
        lst.add(new BasicDBObject().append("_id", "swe").append(Lang.NAME_COLUMN, "swe"));
        lst.add(new BasicDBObject().append("_id", "jpn").append(Lang.NAME_COLUMN, "jpn"));
        lst.add(new BasicDBObject().append("_id", "en").append(Lang.NAME_COLUMN, "en"));
        lst.add(new BasicDBObject().append("_id", "fil").append(Lang.NAME_COLUMN, "fil"));
        lst.add(new BasicDBObject().append("_id", "rum").append(Lang.NAME_COLUMN, "rum"));
        lst.add(new BasicDBObject().append("_id", "pol").append(Lang.NAME_COLUMN, "pol"));
        lst.add(new BasicDBObject().append("_id", "mul").append(Lang.NAME_COLUMN, "mul"));
        lst.add(new BasicDBObject().append("_id", "ara").append(Lang.NAME_COLUMN, "ara"));
        lst.add(new BasicDBObject().append("_id", "nor").append(Lang.NAME_COLUMN, "nor"));
        lst.add(new BasicDBObject().append("_id", "vie").append(Lang.NAME_COLUMN, "vie"));
        lst.add(new BasicDBObject().append("_id", "por").append(Lang.NAME_COLUMN, "por"));
        lst.add(new BasicDBObject().append("_id", "ger").append(Lang.NAME_COLUMN, "ger"));
        lst.add(new BasicDBObject().append("_id", "ita").append(Lang.NAME_COLUMN, "ita"));
        lst.add(new BasicDBObject().append("_id", "tur").append(Lang.NAME_COLUMN, "tur"));
        lst.add(new BasicDBObject().append("_id", "fre").append(Lang.NAME_COLUMN, "fre"));
        lst.add(new BasicDBObject().append("_id", "rus").append(Lang.NAME_COLUMN, "rus"));
        lst.add(new BasicDBObject().append("_id", "dan").append(Lang.NAME_COLUMN, "dan"));
        lst.add(new BasicDBObject().append("_id", "per").append(Lang.NAME_COLUMN, "per"));
        lst.add(new BasicDBObject().append("_id", "nl").append(Lang.NAME_COLUMN, "nl"));
        lst.add(new BasicDBObject().append("_id", "eng").append(Lang.NAME_COLUMN, "eng"));
        lst.add(new BasicDBObject().append("_id", "en-GB").append(Lang.NAME_COLUMN, "en-GB"));
        myCollection.insert(lst);
    }


    @ChangeSet(order = "0001", id = "0001-authority-test-data", author = "iborisenko")
    public void insertAuthority(DB db) {
        DBCollection myCollection = db.getCollection(Authority.TABLE);
        BasicDBObject adminAuth = new BasicDBObject().append("_id", "0-2").append(Authority.NAME_COLUMN, "ROLE_ADMIN");
        BasicDBObject userAuth = new BasicDBObject().append("_id", "0-3").append(Authority.NAME_COLUMN, "ROLE_USER");
        BasicDBObject anonAuth = new BasicDBObject().append("_id", "0-4").append(Authority.NAME_COLUMN, "ROLE_ANONYMOUS");
        myCollection.insert(Arrays.asList(adminAuth, userAuth, anonAuth));
    }


    @ChangeSet(order = "0010", id = "0010-users-test-data", author = "iborisenko")
    public void updateAdminAuthority(MongoTemplate mongoTemplate) {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin@localhost");
        admin.setPassword("$2a$10$y6KLN2tdH.FENVnQnFYDKehU4bXB8u7UYbMhTkxF/.WUQfoiabDnK");
        admin.setEmailVerified(true);
        admin.setProvider(Authority.AuthProvider.local);


        Authority adminAuthority = mongoTemplate.findById("0-2", Authority.class);

        Set<Authority> auths = new HashSet<>();
        auths.add(adminAuthority);
        admin.setAuthorities(auths);
        mongoTemplate.save(admin);
    }
}
