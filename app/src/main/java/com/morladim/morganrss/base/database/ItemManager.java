package com.morladim.morganrss.base.database;

import android.text.TextUtils;
import android.util.Xml;

import com.morladim.morganrss.base.database.dao.ItemDao;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.rss2.Rss2Item;

import org.greenrobot.greendao.annotation.NotNull;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

/**
 * item表管理类
 * <br>创建时间：2017/7/17.
 *
 * @author morladim
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ItemManager extends BaseTableManager<Item, ItemDao> {

    private volatile static ItemManager instance;

    public static final Byte LOCK = 0;

    public static ItemManager getInstance() {
        if (instance == null) {
            synchronized (ItemManager.class) {
                if (instance == null) {
                    instance = new ItemManager();
                }
            }
        }
        return instance;
    }

    @Override
    protected ItemDao getDao() {
        return DbManager.getDaoSession().getItemDao();
    }

    public void insertOrUpdateList(List<Rss2Item> rss2ItemList, Long channelId) {
        if (channelId == null || rss2ItemList == null || rss2ItemList.size() == 0) {
            return;
        }
        for (Rss2Item item : rss2ItemList) {
            insertOrUpdate(item, channelId);
        }
    }

    public void insertOrUpdate(Rss2Item rss2Item, long channelId) {
        long itemId;
        synchronized (LOCK) {
            Item itemInDB = getDao().queryBuilder().where(ItemDao.Properties.ChannelId.eq(channelId), ItemDao.Properties.Link.eq(rss2Item.link), ItemDao.Properties.Title.eq(rss2Item.title)).unique();
            if (itemInDB == null) {
                itemId = insert(convertXmlToEntity(rss2Item, channelId));
            } else {
                updateEntityFromXml(rss2Item, itemInDB);
                itemId = itemInDB.getId();
            }
        }
        CategoryManager.getInstance().insertOrUpdateList(rss2Item.categoryList, itemId);
    }

    public Item convertXmlToEntity(@NotNull Rss2Item rss2Item, long channelId) {
        if (rss2Item.title == null) {
            return null;
        }

        Item item = new Item();
        item.setChannelId(channelId);
        item.setCommentRss(rss2Item.commentRss);
        item.setComments(getCommonsFromXml(rss2Item.commentList));
        item.setContent(rss2Item.encoded);
        Date date = new Date();
        item.setCreateAt(date);
        item.setUpdateAt(date);
        item.setCreator(rss2Item.creator == null ? rss2Item.author : rss2Item.creator);
        item.setDescription(rss2Item.description);
        item.setGuid(rss2Item.guid);
        item.setLink(rss2Item.link);
        item.setPubDate(rss2Item.pubDate);
        item.setTitle(rss2Item.title);
        setItemImage(item, rss2Item);

        return item;
    }

    /**
     * 获取内容中第一张图片信息
     */
    private void setItemImage(Item item, Rss2Item rss2Item) {
        if (hasImage(item, rss2Item)) {
            return;
        }

        String content = rss2Item.encoded == null ? rss2Item.description : rss2Item.encoded;
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (!(event == XmlPullParser.END_DOCUMENT || (event == XmlPullParser.END_TAG && "img".equals(parser.getName())))) {
                if (event == XmlPullParser.START_TAG) {
                    addImage(item, parser);
                }
                try {
                    event = parser.next();
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImage(Item item, XmlPullParser parser) {
        if ("img".equals(parser.getName())) {
            int count = parser.getAttributeCount();
            String imageUrl = null, imageWidth = null, imageHeight = null, dataRawWidth = null, dataRawHeight = null;
            for (int i = 0; i < count; i++) {
                String key = parser.getAttributeName(i);
                if ("src".equals(key.toLowerCase())) {
                    imageUrl = parser.getAttributeValue(i);
                }
                if ("width".equals(key.toLowerCase())) {
                    imageWidth = parser.getAttributeValue(i);
                }
                if ("height".equals(key.toLowerCase())) {
                    imageHeight = parser.getAttributeValue(i);
                }
                if ("data-rawwidth".equals(key.toLowerCase())) {
                    dataRawWidth = parser.getAttributeValue(i);
                }
                if ("data-rawheight".equals(key.toLowerCase())) {
                    dataRawHeight = parser.getAttributeValue(i);
                }
            }
            item.setImageUrl(imageUrl);
            item.setImageWidth(imageWidth == null ? dataRawWidth : imageWidth);
            item.setImageHeight(imageHeight == null ? dataRawHeight : imageHeight);
        }
    }

    private boolean hasImage(Item item, Rss2Item rss2Item) {
        String image = TextUtils.isEmpty(rss2Item.image) ? rss2Item.focusPic : rss2Item.image;
        if (!TextUtils.isEmpty(image)) {
            item.setImageUrl(image);
            return true;
        }
        return false;
    }

    /**
     * 获取commonList中最长的为commons
     */
    private String getCommonsFromXml(List<String> commonList) {
        if (commonList == null) {
            return null;
        }
        int longestI = 0;
        int length = 0;
        int temPLength;
        for (int i = 0; i < commonList.size(); i++) {
            temPLength = commonList.get(i).length();
            if (temPLength > length) {
                longestI = i;
            }
        }
        return commonList.get(longestI);
    }

    private void updateEntityFromXml(Rss2Item rss2Item, Item item) {
        if (item == null) {
            return;
        }
        item.setPubDate(rss2Item.pubDate);
        item.setDescription(rss2Item.description);
        item.setComments(rss2Item.encoded);
        item.setUpdateAt(new Date());
        item.resetCategoryList();
    }

    public List<Item> getList(int offset, int limit) {
        return getDao().queryBuilder().offset(offset).limit(limit).orderDesc(ItemDao.Properties.PubDate).list();
    }

    public List<Item> getList(long channelId, int offset, int limit) {
        return getDao().queryBuilder().where(ItemDao.Properties.ChannelId.eq(channelId)).offset(offset).limit(limit).orderDesc(ItemDao.Properties.PubDate).list();
    }

    public List<Item> getListFrom(long channelId, long itemId, int limit) {
        return getDao().queryBuilder().where(ItemDao.Properties.ChannelId.eq(channelId), ItemDao.Properties.Id.gt(itemId)).limit(limit).orderDesc(ItemDao.Properties.PubDate).list();
    }
}

