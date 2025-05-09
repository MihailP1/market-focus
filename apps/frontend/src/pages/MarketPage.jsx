import useNewsSocket from "../hooks/useNewsSocket";

export default function MarketPage() {
  const newsList = useNewsSocket();

  console.log("News List:", newsList);  // Логируем список новостей для отладки

  return (
    <div className="p-4 grid grid-cols-2 gap-4">
      {/* Левая колонка — Новости */}
      <div className="bg-white p-4 rounded shadow h-[80vh] overflow-y-auto">
        <h2 className="text-xl font-bold mb-4">Финансовые новости</h2>
        <ul className="space-y-4">
          {newsList.length > 0 ? (
            newsList.map((newsItem, index) => (
              <li key={index} className="border-b pb-4">
                {/* Выводим только ключевые данные */}
                <h3 className="font-semibold text-lg">{newsItem.title}</h3>
                {/* <p className="text-gray-600">{newsItem.description}</p> */}
                <a
                  href={newsItem.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-500 hover:underline"
                >
                  Читать статью
                </a>
              </li>
            ))
          ) : (
            <li className="text-center">Нет доступных новостей</li>
          )}
        </ul>
      </div>

      {/* Правая колонка — Котировки (заглушка) */}
      <div className="bg-white p-4 rounded shadow">
        <h2 className="text-xl font-bold mb-4">Котировки</h2>
        <p>Здесь будут отображаться котировки.</p>
      </div>
    </div>
  );
}
