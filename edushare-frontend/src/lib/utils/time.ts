// src/lib/utils/time.ts
export function formatTimeAgo(isoString: string | null): string {
	if (!isoString) return '';
	const date = new Date(isoString);
	if (isNaN(date.getTime())) return '';
	const now = new Date();
	const elapsedSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

	const rtf = new Intl.RelativeTimeFormat('vi', { numeric: 'auto' });

	if (elapsedSeconds < 60) {
		return 'vừa xong';
	}
	if (elapsedSeconds < 3600) {
		return rtf.format(-Math.floor(elapsedSeconds / 60), 'minute'); // e.g., "5 phút trước"
	}
	if (elapsedSeconds < 86400) {
		return rtf.format(-Math.floor(elapsedSeconds / 3600), 'hour'); // e.g., "2 giờ trước"
	}
	if (elapsedSeconds < 2592000) {
		return rtf.format(-Math.floor(elapsedSeconds / 86400), 'day'); // e.g., "1 ngày trước"
	}

	return date.toLocaleDateString('vi-VN');
}
