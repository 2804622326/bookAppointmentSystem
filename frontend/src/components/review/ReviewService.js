import { api } from "../utils/api";

export async function addReview(vetId, reviewerId, reviewData) {
  try {
    // Auth token will be automatically added by axios interceptor
    const response = await api.post(
      `reviews/submit-review?vetId=${vetId}&reviewerId=${reviewerId}`,
      reviewData
    );
    return response.data;
  } catch (error) {
    throw error;
  }
}
