import useNewsSocket from "../hooks/useNewsSocket";

export default function MarketPage() {
  const { news, quotes } = useNewsSocket();

  console.log("News List:", news);     // Логируем новости
  console.log("Quotes List:", quotes); // Логируем котировки

  return (
    <div className="p-4 grid grid-cols-2 gap-4">
      {/* Левая колонка — Новости */}
      <div className="bg-white p-4 rounded shadow h-[80vh] overflow-y-auto">
        <h2 className="text-xl font-bold mb-4">Финансовые новости</h2>
        <ul className="space-y-4">
          {news.length > 0 ? (
            news.map((newsItem, index) => (
              <li key={index} className="border-b pb-4">
                <h3 className="font-semibold text-lg">{newsItem.title}</h3>
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

      {/* Правая колонка — Котировки */}
      <div className="bg-white p-4 rounded shadow h-[80vh] overflow-y-auto">
        <h2 className="text-xl font-bold mb-4">Котировки</h2>
        <ul className="space-y-4">
          {quotes.length > 0 ? (
            quotes.map((quote, index) => (
              <li key={index} className="border-b pb-4">
                <div className="font-semibold text-lg">{quote.symbol}</div>
                <div>Цена: {quote.price.toFixed(2)}</div>
                <div>Время: {new Date(quote.timestamp).toLocaleTimeString()}</div>
              </li>
            ))
          ) : (
            <li className="text-center">Нет доступных котировок</li>
          )}
        </ul>

      </div>
    </div>
  );
}
