// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

error CustomError();

contract TicketNFT is ERC721, Ownable {
    enum TicketStatus { Valid, Used, Canceled }
    mapping(uint256 => TicketStatus) public ticketStatus;

    struct TicketInfo {
        uint256 visitDate; // Unix timestamp
        string park;
        string sku;
    }
    mapping(uint256 => TicketInfo) public ticketInfo;

    // Mapping from account to owned token IDs
    mapping(address => uint256[]) private _ownedTokens;

    uint256 public nextTokenId;

    // Add the event for minting
    event TicketMinted(address indexed to, uint256 indexed tokenId, uint256 visitDate, string park, string sku);

    constructor(address initialOwner) ERC721("ThemeParkTicket", "TPTKT") Ownable(initialOwner) {}

    function mint(address to, uint256 visitDate, string memory park, string memory sku) public onlyOwner {
        uint256 tokenId = nextTokenId;
        _safeMint(to, tokenId);
        ticketStatus[tokenId] = TicketStatus.Valid;
        require(ticketInfo[tokenId].visitDate == 0, "TicketInfo already set");
        ticketInfo[tokenId] = TicketInfo(visitDate, park, sku);
        nextTokenId++;
        emit TicketMinted(to, tokenId, visitDate, park, sku);
    }

    // Internal function to remove a tokenId from an owner's _ownedTokens array
    function _removeTokenFromOwnerEnumeration(address from, uint256 tokenId) private {
        uint256 length = _ownedTokens[from].length;
        for (uint256 i = 0; i < length; i++) {
            if (_ownedTokens[from][i] == tokenId) {
                _ownedTokens[from][i] = _ownedTokens[from][length - 1];
                _ownedTokens[from].pop();
                break;
            }
        }
    }

    // Override _update to update _ownedTokens mapping (OpenZeppelin 5.x extension point)
    function _update(address to, uint256 tokenId, address auth) internal override returns (address) {
        address from = super._update(to, tokenId, auth);
        if (from != address(0)) {
            _removeTokenFromOwnerEnumeration(from, tokenId);
        }
        if (to != address(0)) {
            _ownedTokens[to].push(tokenId);
        }
        return from;
    }

    function useTicket(uint256 tokenId) public {
        require(ticketStatus[tokenId] == TicketStatus.Valid, "Ticket not valid");
        ticketStatus[tokenId] = TicketStatus.Used;
    }

    function cancelTicket(uint256 tokenId) public onlyOwner {
        require(ticketStatus[tokenId] == TicketStatus.Valid, "Ticket not valid");
        ticketStatus[tokenId] = TicketStatus.Canceled;
    }

    function getTicketStatus(uint256 tokenId) public view returns (TicketStatus) {
        return ticketStatus[tokenId];
    }

    function getTicketInfo(uint256 tokenId) public view returns (uint256, string memory, string memory) {
        TicketInfo memory info = ticketInfo[tokenId];
        return (info.visitDate, info.park, info.sku);
    }

    function getTicketsOf(address account) public view returns (uint256[] memory) {
        return _ownedTokens[account];
    }
}