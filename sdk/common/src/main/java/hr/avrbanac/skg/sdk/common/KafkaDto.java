package hr.avrbanac.skg.sdk.common;

import com.fasterxml.jackson.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Main DTO structure for use with all the SDK modules in need of transporting data over to another MS or some other third party system.
 * The idea with DTO implementing {@link Map} or extending {@link HashMap} is not working very well with JSON SerDe (when Jackson detects
 * map it will force map serde and the rest of the object will remain invisible). With facade approach, extending class needs to know
 * internals of this DTO, which in turn makes inheritance broken. This leaves us with having inner map solution and some wrapper methods to
 * work with. Inner map element will also be protected, so any extending classes can easily access it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class KafkaDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 20240911144000L;

    @JsonProperty
    @JsonInclude
    protected Map<String, Object> innerMap = new HashMap<>();

    private long createdTime = System.currentTimeMillis();
    protected long receivedTime;
    protected String id;
    protected String qualifier;
    protected String topic;
    private UUID uuid = UUID.randomUUID();
    private UUID tag;
    protected KafkaDtoType type = KafkaDtoType.GENERAL;
    protected KafkaDtoStatus status = KafkaDtoStatus.INIT;
    protected long statusCode = 0L;

    /**
     * Private CTOR accessed through reflection.
     */
    private KafkaDto() { }

    /**
     * Private CTOR accessed by Jackson.
     * @param innerMap {@link Map}
     * @param createdTime long
     * @param receivedTime long
     * @param id {@link String}
     * @param qualifier {@link String}
     * @param topic {@link String}
     * @param uuid {@link UUID}
     * @param tag {@link UUID}
     * @param type {@link KafkaDtoType}
     * @param status {@link KafkaDtoStatus}
     * @param statusCode long
     */
    @JsonCreator
    private KafkaDto(
            @JsonProperty("innerMap") final Map<String, Object> innerMap,
            @JsonProperty("createdTime") final long createdTime,
            @JsonProperty("receivedTime") final long receivedTime,
            @JsonProperty("id") final String id,
            @JsonProperty("qualifier") final String qualifier,
            @JsonProperty("topic") final String topic,
            @JsonProperty("uuid") final UUID uuid,
            @JsonProperty("tag") final UUID tag,
            @JsonProperty("type") final KafkaDtoType type,
            @JsonProperty("status") final KafkaDtoStatus status,
            @JsonProperty("statusCode") final long statusCode) {

        this.innerMap = innerMap;
        this.createdTime = createdTime;
        this.receivedTime = receivedTime;
        this.id = id;
        this.qualifier = qualifier;
        this.topic = topic;
        this.uuid = uuid;
        this.tag = tag;
        this.type = type;
        this.status = status;
        this.statusCode = statusCode;
    }

    /**
     * CTOR for extending classes.
     * @param type {@link KafkaDtoType} new type
     * @param originalKafkaDto {@link KafkaDto} original DTO from which tag is inherited
     */
    protected KafkaDto(
            final KafkaDtoType type,
            final KafkaDto originalKafkaDto) {

        this.type = type;
        this.tag = originalKafkaDto.tag;
    }

    /**
     * Protected CTOR for the use with extending classes. (Tag and uuid will be the same.
     * @param type {@link KafkaDtoType} of the newly created object
     */
    protected KafkaDto(final KafkaDtoType type) {
        this.type = type;
        this.tag = uuid;
    }


    // GETTERS

    public Map<String, Object> getInnerMap() {
        return innerMap;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public String getId() {
        return id;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getTopic() {
        return topic;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getTag() {
        return tag;
    }

    public KafkaDtoType getType() {
        return type;
    }

    public KafkaDtoStatus getStatus() {
        return status;
    }

    public long getStatusCode() {
        return statusCode;
    }


    // SETTERS

    public KafkaDto setReceivedTime(final long receivedTime) {
        this.receivedTime = receivedTime;
        return this;
    }

    public KafkaDto setId(final String id) {
        this.id = id;
        return this;
    }

    public KafkaDto setQualifier(final String qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    public KafkaDto setTopic(final String topic) {
        this.topic = topic;
        return this;
    }

    public KafkaDto setType(final KafkaDtoType type) {
        this.type = type;
        return this;
    }

    public KafkaDto setStatus(final KafkaDtoStatus status) {
        this.status = status;
        return this;
    }

    public KafkaDto setStatusCode(final long statusCode) {
        this.statusCode = statusCode;
        return this;
    }


    // ADDITIONAL MODIFY METHODS

    public KafkaDto putAllFrom(final KafkaDto sourceDto) {
        this.innerMap.putAll(sourceDto.getInnerMap());
        return this;
    }

    public KafkaDto putFrom(final String key, final KafkaDto sourceDto) {
        this.innerMap.put(key, sourceDto.get(key));
        return this;
    }

    public KafkaDto addStatusCode(final long addStatusCode) {
        this.statusCode = this.statusCode | addStatusCode;
        return this;
    }


    // FOLLOWING METHODS ARE ALL DEFINED BY MAP INTERFACE; BECAUSE OF THE COMPLICATIONS WITH JACKSON, DTO DOESN'T
    // IMPLEMENT MAP, BUT ALL THE METHODS FROM INTERFACE ARE HERE; VOID METHODS RETURN DTO INSTANCE INSTEAD.

    //    @Override
    public int size() {
        return innerMap.size();
    }

    //    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    //    @Override
    public boolean containsKey(final String key) {
        return innerMap.containsKey(key);
    }

    //    @Override
    public boolean containsValue(final Object value) {
        return innerMap.containsValue(value);
    }

    //    @Override
    public Object get(final String key) {
        return innerMap.get(key);
    }

    //    @Override
    public Object put(
            final String key,
            final Object value) {

        return innerMap.put(key, value);
    }

    //    @Override
    public Object remove(final String key) {
        return innerMap.remove(key);
    }

    /**
     * Puts all from provided map to {@link KafkaDto} instance inner map. In a way this is inherited from Map interface.
     * @param map {@link Map} provided payload to be put into {@link KafkaDto} inner map
     * @return {@link KafkaDto} this instance
     */
    public KafkaDto putAll(final Map<? extends String, ?> map) {
        innerMap.putAll(map);
        return this;
    }

    /**
     * Clears inner map of the {@link KafkaDto} instance.
     * @return {@link KafkaDto} this instance
     */
    public KafkaDto clear() {
        innerMap.clear();
        return this;
    }

    //    @Override
    public Set<String> keySet() {
        return innerMap.keySet();
    }

    //    @Override
    public Collection<Object> values() {
        return innerMap.values();
    }

    //    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return innerMap.entrySet();
    }


    // REST OF THE METHODS

    /**
     * Constructor for {@link KafkaDto} is locked, so this is one way to create new CdDto. Newly created object will hav tag field unique
     * together with the unique uuid.
     * @param type {@link KafkaDtoType} of the newly created {@link KafkaDto}
     * @return newly created {@link KafkaDto}
     * @see #createFrom(KafkaDto) another method which can be used to create new {@link KafkaDto}
     */
    public static KafkaDto createNew(final KafkaDtoType type) {
        return new KafkaDto(type);
    }

    /**
     * Constructor for {@link KafkaDto} is locked, so this is one way to create new CdDto. Newly created object will have tag same as the
     * provided original.
     * @param originalKafkaDto original {@link KafkaDto} object from with new one will be created
     * @return newly created {@link KafkaDto}
     * @see #createNew(KafkaDtoType) another method which can be used to create new {@link KafkaDto}
     */
    public static KafkaDto createFrom(final KafkaDto originalKafkaDto) {
        return new KafkaDto(originalKafkaDto.getType(), originalKafkaDto);
    }

    @Override
    public String toString() {

        return "KafkaDto{" +
                "innerMap=" + innerMap +
                ", createdTime=" + createdTime +
                ", receivedTime=" + receivedTime +
                ", id='" + id + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", topic='" + topic + '\'' +
                ", uuid=" + uuid +
                ", tag=" + tag +
                ", type=" + type +
                ", status=" + status +
                ", statusCode=" + statusCode +
                '}';
    }
}
