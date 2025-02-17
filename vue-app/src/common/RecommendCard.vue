<!-- <template>
    <div class="card">
        <div class="card-image">
            <img :src="props.imageUrl || '/default-image.png'" :alt="props.title">
        </div>
        <div class="card-content">
            <h3 class="card-title">{{ props.title || 'No Title' }}</h3>
            <p class="card-description">{{ props.description || 'No Description' }}</p>
            <div class="card-footer">
                <div class="reading-time">
                    <i class="far fa-clock"></i>
                    {{ props.readingTime }}분
                </div>
                <a :href="props.url" target="_blank" class="url-link">
                    <i class="fas fa-external-link-alt"></i>
                </a>
            </div>
        </div>
    </div>
</template>

<script setup>
const props = defineProps({
    url: {
        type: String,
        required: true
    },
    title: {
        type: String,
        default: ''
    },
    description: {
        type: String,
        default: ''
    },
    imageUrl: {
        type: String,
        default: ''
    },
    readingTime: {
        type: Number,
        required: true
    }
});
</script>

<style scoped>
.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: transform 0.2s;
}

.card:hover {
    transform: translateY(-4px);
}

.card-image {
    width: 100%;
    height: 160px;
    overflow: hidden;
}

.card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.card-content {
    padding: 16px;
}

.card-title {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0 0 8px 0;
    color: #2c3e50;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-description {
    font-size: 0.9rem;
    color: #666;
    margin: 0 0 16px 0;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 8px;
    border-top: 1px solid #eee;
}

.reading-time {
    font-size: 0.9rem;
    color: #666;
}

.reading-time i {
    margin-right: 4px;
}

.url-link {
    color: #007bff;
    text-decoration: none;
}

.url-link:hover {
    color: #0056b3;
}
</style>  -->